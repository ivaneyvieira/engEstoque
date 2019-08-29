package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.DEPOSITO
import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.Repositories
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.StatusNota.RECEBIDO
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.TipoNota.OUTROS_E
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_E
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_S
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_E
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_S
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.ETipoGrupo.BLUE
import br.com.engecopi.estoque.viewmodel.ETipoGrupo.GREEN
import br.com.engecopi.estoque.viewmodel.ETipoGrupo.RED
import br.com.engecopi.estoque.viewmodel.ETipoGrupo.SELECT_FT
import br.com.engecopi.estoque.viewmodel.ETipoGrupo.WHITE
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.utils.localDate
import java.time.LocalDate
import java.time.LocalDateTime

abstract class NotaViewModel<VO: NotaVo>(view: IView,
                                         val tipo: TipoMov,
                                         private val statusDefault: StatusNota,
                                         private val statusImpressao: StatusNota,
                                         private val abreviacaoNota: String): CrudViewModel<ItemNota, QItemNota, VO>(
  view) {
  override fun update(bean: VO) {
    if(bean.localizacao?.localizacao.isNullOrBlank())
      throw EViewModel("Não foi especificado a localização do item")
    val nota = updateNota(bean)
    val produto = saveProduto(bean.produto)

    updateItemNota(bean, nota, produto)
  }

  override fun QItemNota.orderQuery(): QItemNota {
    return this.order()
      .nota.lancamento.desc()
      .nota.id.desc()
  }

  override fun add(bean: VO) {
    val nota = insertNota(bean)
    val usuario = bean.usuario
    if(bean.notaSaci == null) {
      val produto = saveProduto(bean.produto)
      if(Nota.itemDuplicado(nota, produto)) {
        val msg = "O produto ${produto.codigo} - ${produto.descricao}. Já foi inserido na nota ${nota.numero}."
        view.showWarning(msg)
      }
      else insertItemNota(nota, produto, bean.quantProduto ?: 0, usuario, bean.localizacao?.localizacao)
    }
    else {
      val produtos = bean.produtos.filter {it.selecionado && it.quantidade != 0}
      val produtosJaInserido = produtos.asSequence()
        .distinctBy {it.produto?.id}
        .filter {prd ->
          prd.produto.let {Nota.itemDuplicado(nota, it)} ?: false
        }
        .map {it.produto}
        .filterNotNull()
      produtosJaInserido.forEach {prd ->
        val msg = "O produto ${prd.codigo} - ${prd.descricao}. Já foi inserido na nota ${nota.numero}."
        view.showWarning(msg)
      }
      produtos.filter {it.produto !in produtosJaInserido}
        .forEach {produto ->
          produto.let {prd ->
            if(usuario.temProduto(prd.produto)) insertItemNota(nota,
                                                               prd.produto,
                                                               prd.quantidade,
                                                               usuario,
                                                               prd.localizacao?.localizacao)
          }
        }
    }
    val item = nota.itensNota().firstOrNull()
    bean.entityVo = item
  }

  private fun insertItemNota(nota: Nota,
                             produto: Produto?,
                             quantProduto: Int,
                             usuario: Usuario,
                             local: String?): ItemNota? {
    if(local.isNullOrBlank()) throw EViewModel("Não foi especificado a localização do item")
    val saldoLocal = produto?.saldoLoja(local) ?: 0
    return if(quantProduto != 0) {
      when {
        (saldoLocal + (statusDefault.multiplicador * quantProduto)) < 0 -> {
          val msg = "Saldo insuficiente para o produto ${produto?.codigo} - ${produto?.descricao}."
          view.showWarning(msg)
          null
        }
        else                                                            -> {
          val item = ItemNota()
          item.apply {
            this.nota = nota
            this.produto = produto
            this.quantidade = quantProduto
            this.usuario = usuario
            this.localizacao = local
            this.status = statusDefault
          }
          item.insert()
          item.produto?.recalculaSaldos(local)
          item
        }
      }
    }
    else null
  }

  private fun updateItemNota(bean: VO, nota: Nota, produto: Produto?) {
    bean.toEntity()
      ?.let {item ->
        item.apply {
          this.nota = nota
          this.produto = produto
          this.quantidade = bean.quantProduto ?: 0
          this.status = bean.status!!
        }
        item.update()
        item.produto?.recalculaSaldos(bean.localizacao?.localizacao ?: "")
      }
  }

  private fun saveProduto(produto: Produto?): Produto {
    produto ?: throw EViewModel("Produto não encontrado no saci")
    return produto.apply {
      save()
    }
  }

  private fun updateNota(bean: VO): Nota {
    return saveNota(bean)
  }

  private fun insertNota(bean: VO): Nota {
    return saveNota(bean)
  }

  private fun saveNota(bean: VO): Nota {
    val nota: Nota = bean.nota ?: Nota()
    nota.apply {
      this.numero = if(bean.numeroNF.isNullOrBlank()) "${Nota.novoNumero()}"
      else bean.numeroNF ?: ""
      this.tipoMov = tipo
      this.tipoNota = bean.tipoNota
      this.loja = bean.lojaNF
      this.data = bean.dataNota
      this.dataEmissao = bean.dataEmissao
      this.observacao = bean.observacaoNota ?: ""
      this.rota = bean.rota ?: ""
      this.fornecedor = bean.fornecedor
      this.cliente = bean.cliente
    }
    nota.save()
    return nota
  }

  override val query: QItemNota
    get() {
      Repositories.updateViewProdutosLoc()
      return ItemNota.where()
        .setUseQueryCache(true)
        .fetch("nota")
        .fetch("usuario")
        .fetch("produto")
        .fetch("produto.vproduto")
        .fetch("produto.viewProdutoLoc")
        .nota.tipoMov.eq(tipo)
        .filtroStatus()
        .nota.loja.id.eq(lojaDefault.id)
        .let {query ->
          if(abreviacaoNota == "") query
          else query.localizacao.startsWith(abreviacaoNota)
        }
    }

  abstract fun createVo(): VO

  override fun ItemNota.toVO(): VO {
    val itemNota = this
    return createVo().apply {
      readOnly = true
      entityVo = itemNota
      val nota = itemNota.nota
      this.numeroNF = nota?.numero
      this.numeroCodigo = itemNota.codigoBarraConferencia
      this.numeroCodigoReduzido = itemNota.codigoBarraCliente
      this.lojaNF = nota?.loja
      this.observacaoNota = nota?.observacao
      this.produto = itemNota.produto
      this.tipoNota = itemNota.nota?.tipoNota ?: OUTROS_E
      this.rota = nota?.rota
      this.usuario = itemNota.usuario ?: usuarioDefault
      this.localizacao = LocProduto(itemNota.localizacao)
      this.status = itemNota.status
      readOnly = false
    }
  }

  override fun QItemNota.filterString(text: String): QItemNota {
    val idLoja = RegistryUserInfo.lojaDefault.id
    return nota.numero.startsWith(text)
      .codigoBarraCliente.startsWith(text)
      .and()
      .produto.viewProdutoLoc.localizacao.contains(text)
      .produto.viewProdutoLoc.loja.id.eq(idLoja)
      .endAnd()
      .produto.vproduto.codigo.contains(" $text")
      .produto.vproduto.nome.contains(text)
  }

  override fun QItemNota.filterDate(date: LocalDate): QItemNota {
    return nota.data.eq(date)
  }

  override fun delete(bean: VO) {
    bean.toEntity()
      ?.also {item ->
        item.delete()
      }
  }

  fun findLojas(loja: Loja?): List<Loja> = execList {
    loja?.let {listOf(it)} ?: Loja.all()
  }

  fun localizacaoes(): List<String> {
    return ViewProdutoLoc.localizacoesAbreviacaoCache(abreviacaoNota)
  }

  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta) = execString {
    itemNota ?: return@execString ""
    val tipoNota = itemNota.tipoNota ?: return@execString ""
    if(!etiqueta.imprimivel(tipoNota)) return@execString ""
    val print = itemNota.printEtiqueta()
    if(!usuarioDefault.admin) itemNota.let {
      it.refresh()
      it.impresso = true
      it.update()
    }
    print.print(etiqueta.template)
  }

  fun imprimir(itemNota: ItemNota?, notaCompleta: Boolean) = execString {
    itemNota ?: return@execString ""
    if(notaCompleta) {
      val itens = ItemNota.where()
        .nota.eq(itemNota.nota)
        .status.eq(itemNota.status)
        .order()
        .nota.loja.numero.asc()
        .nota.numero.asc()
        .findList()
      imprimir(itens)
    }
    else
      imprimir(listOf(itemNota))
  }

  fun imprimir() = execString {
    val itens = ItemNota.where()
      .let {q -> if(usuarioDefault.admin) q else q.impresso.eq(false)}
      .status.eq(statusImpressao)
      .order()
      .nota.loja.numero.asc()
      .nota.numero.asc()
      .findList()
    imprimir(itens)
  }

  fun imprimir(itens: List<ItemNota>) = execString {
    val etiquetas = Etiqueta.findByStatus(statusImpressao)

    etiquetas.joinToString(separator = "\n") {etiqueta ->
      imprimir(itens, etiqueta)
    }
  }

  private fun imprimir(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.filter {it.abreviacao?.abreviacao == abreviacaoDefault}
      .map {imprimir(it, etiqueta)}
      .distinct()
      .joinToString(separator = "\n")
  }

  abstract fun QItemNota.filtroStatus(): QItemNota

  fun desfazOperacao(item: ItemNota?) = exec {
    item?.desfazerOperacao()
  }
}

abstract class NotaVo(val tipo: TipoMov, private val abreviacaoNota: String): EntityVo<ItemNota>() {
  override fun findEntity(): ItemNota? {
    return ItemNota.find(nota, produto)
  }

  var usuario: Usuario = usuarioDefault
  var numeroCodigo: String? = ""
  var numeroCodigoReduzido: String? = ""
  var numeroNF: String? = ""
    set(value) {
      if(field != value) {
        field = value
        atualizaNota()
      }
    }
  var lojaNF: Loja? = null
    set(value) {
      if(field != value) {
        field = value
        atualizaNota()
      }
    }
  var tipoNota: TipoNota = OUTROS_E
  val temGrid
    get() = (tipoNota != OUTROS_E) && (entityVo == null)
  val naoTemGrid
    get() = !temGrid
  var rota: String? = ""
  val rotaDescricao: String?
    get() = if(tipoNota == TRANSFERENCIA_E || tipoNota == TRANSFERENCIA_S) rota
    else ""
  private val mapNotaSaci = mutableMapOf<String?, List<NotaSaci>>()
  private val notaProdutoSaci: List<NotaSaci>
    get() = if(entityVo == null) mapNotaSaci.getOrPut(numeroNF) {
      when(tipo) {
        SAIDA   -> Nota.findNotaSaidaSaci(numeroNF).filter {
          usuario.admin || (it.tipo != "PEDIDO_E")
        }
        ENTRADA -> Nota.findNotaEntradaSaci(numeroNF)
      }
    }
    else emptyList()
  val notaSaci
    get() = notaProdutoSaci.firstOrNull()
  val nota: Nota?
    get() = entityVo?.nota ?: Nota.findNota(numeroNF, tipo)

  private fun atualizaNota() {
    if(!readOnly && entityVo == null) {
      val nota = notaSaci ?: return
      tipoNota = TipoNota.value(nota.tipo) ?: OUTROS_E
      rota = nota.rota
      produtos.clear()
      val produtosVo = notaProdutoSaci.flatMap {notaSaci ->
        if(tipoNota.tipoMov == SAIDA) listItensSaida(notaSaci)
        else listItensEntrada(notaSaci)
      }
      produtos.addAll(produtosVo.asSequence().filter {
        it.quantidade != 0 && it.codigo != "" && it.localizacao?.localizacao?.startsWith(abreviacaoNota) ?: false
      }.sortedWith(compareBy(ProdutoVO::isSave, ProdutoVO::codigo, ProdutoVO::grade, ProdutoVO::localizacao)).toList())
    }
  }

  private fun listItensEntrada(notaSaci: NotaSaci): List<ProdutoVO> {
    val prd = Produto.findProduto(notaSaci.prdno, notaSaci.grade) ?: return emptyList()
    val localizacoes = prd.localizacoes(abreviacaoNota)
      .filter {it.startsWith(abreviacaoNota)}
      .sorted()
    val ultimaLocalizacao = localizacoes.max() ?: ""
    val produtoVo = ProdutoVO(prd, RECEBIDO, LocProduto(ultimaLocalizacao), notaSaci.isSave()).apply {
      quantidade = notaSaci.quant ?: 0
    }
    return listOf(produtoVo)
  }

  private fun listItensSaida(notaSaci: NotaSaci): List<ProdutoVO> {
    val prd = Produto.findProduto(notaSaci.prdno, notaSaci.grade) ?: return emptyList()
    var quant = notaSaci.quant ?: return emptyList()
    val localizacoes = prd.localizacoes(abreviacaoNota)
      .filter {it.startsWith(abreviacaoNota)}
      .sorted()
    val ultimaLocalizacao = localizacoes.max() ?: ""
    val produtosLocais = localizacoes.map {localizacao ->
      ProdutoVO(prd, CONFERIDA, LocProduto(localizacao), notaSaci.isSave()).apply {
        if(quant > 0) if(quant > saldo) {
          if(localizacao == ultimaLocalizacao) {
            quantidade = quant
            quant = 0
          }
          else {
            quantidade = saldo
            quant -= saldo
          }
        }
        else {
          quantidade = quant
          quant = 0
        }
        else quantidade = 0
      }
    }
    return produtosLocais
  }

  val tipoNotaDescricao: String
    get() {
      return if(tipoNota == PEDIDO_E || tipoNota == PEDIDO_S) "Pedido $rota".trim()
      else tipoNota.descricao
    }
  val dataNota: LocalDate
    get() = toEntity()?.dataNota ?: notaSaci?.date?.localDate() ?: LocalDate.now()
  val lancamento: LocalDate
    get() = toEntity()?.data ?: LocalDate.now()
  val horaLacamento: LocalDateTime
    get() = toEntity()?.let {LocalDateTime.of(it.data, it.hora)} ?: LocalDateTime.now()
  val dataEmissao: LocalDate
    get() = toEntity()?.nota?.dataEmissao ?: notaSaci?.dtEmissao?.localDate() ?: LocalDate.now()
  val numeroInterno: Int
    get() = if(entityVo == null) notaSaci?.invno ?: 0
    else 0
  val fornecedor: String
    get() = entityVo?.nota?.fornecedor ?: notaSaci?.vendName ?: ""
  val cliente: String
    get() = entityVo?.nota?.cliente ?: notaSaci?.clienteName ?: ""
  var observacaoNota: String? = ""
  val produtoNota: List<Produto>
    get() {
      if(entityVo != null) return emptyList()
      val nota = notaProdutoSaci
      val produtos = if(nota.isNotEmpty()) nota.asSequence().mapNotNull {notaSaci ->
        Produto.findProduto(notaSaci.prdno, notaSaci.grade)
      }.filter {produto ->
        usuario.temProduto(produto)
      }.toList()
      else ViewProdutoLoc.produtosCache() // Produto.all().filter { usuario.temProduto(it) }
      return produtos.sortedBy {it.codigo + it.grade}
    }
  val quantidadeReadOnly
    get() = notaSaci != null
  val itemNota
    get() = toEntity()
  val produtos = ArrayList<ProdutoVO>()
  var produto: Produto? = null
    set(value) {
      field = value
      quantProduto = toEntity()?.quantidade ?: notaProdutoSaci.firstOrNull {neSaci ->
        (neSaci.prdno ?: "") == (value?.codigo?.trim() ?: "") && (neSaci.grade ?: "") == (value?.grade ?: "")
      }?.quant ?: 0
    }
  val descricaoProduto: String
    get() = produto?.descricao ?: ""
  val codigo: String
    get() = produto?.codigo ?: ""
  val grade: String
    get() = produto?.grade ?: ""
  var quantProduto: Int? = 0
  val saldo: Int
    get() = produto?.saldoLoja(localizacao?.localizacao) ?: 0
  var localizacao: LocProduto? = null
  val localizacaoProduto
    get() = produto?.localizacoes(abreviacaoNota)?.map {LocProduto(it)}.orEmpty()
  var status: StatusNota? = null
}

class ProdutoVO(val produto: Produto, val statusNota: StatusNota, var localizacao: LocProduto?, val isSave: Boolean) {
  val codigo: String = produto.codigo
  val grade: String = produto.grade
  var quantidade: Int = 0
  var selecionado: Boolean = false
  val saldo: Int
    get() = produto.saldoLoja(localizacao?.localizacao) - if(isSave) quantidade * multipicador else 0
  val tipoMov
    get() = statusNota.tipoMov
  val multipicador
    get() = statusNota.multiplicador
  val saldoFinal
    get() = saldo + quantidade * if(tipoMov == ENTRADA) 1 else -1
  val descricaoProduto: String
    get() = produto.descricao ?: ""
  var value: ItemNota? = null
  val gtin
    get() = produto.barcodeGtin
  var dateUpdate: LocalDateTime = LocalDateTime.now()
  var grupoSelecao: ETipoGrupo = WHITE
  val ordermSelecao: Int
    get() = grupoSelecao.ordem

  fun allowSelect(): Boolean {
    val status = this.value?.status ?: return false
    return this.saldoFinal >= 0 && (status == INCLUIDA || status == ENT_LOJA)
  }

  fun allowEdit(): Boolean {
    val nota = this.value?.nota ?: return false
    val status = this.value?.status ?: return false
    return (nota.lancamentoOrigem == DEPOSITO) && (status == INCLUIDA || status == ENT_LOJA)
  }

  fun updateItem(first: Boolean) {
    dateUpdate = LocalDateTime.now()
    grupoSelecao = if(selecionado) {
      if(first)
        SELECT_FT
      else
        BLUE
    }
    else
      if(saldoFinal < 0) RED
      else if(!allowSelect()) GREEN
      else
        WHITE
  }
}

enum class ETipoGrupo(val ordem: Int) {
  SELECT_FT(0),
  RED(1),
  WHITE(2),
  BLUE(3),
  GREEN(4)
}
