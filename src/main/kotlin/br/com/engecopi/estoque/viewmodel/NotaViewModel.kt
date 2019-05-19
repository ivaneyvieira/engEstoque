package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
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
    if(bean.localizacao?.localizacao.isNullOrBlank()) throw EViewModel("Não foi especificado a localização do item")
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
      val produtosJaInserido =
        produtos.asSequence()
          .distinctBy {it.produto?.id}
          .filter {prd ->
            prd.produto?.let {Nota.itemDuplicado(nota, it)} ?: false
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
  }

  private fun insertItemNota(nota: Nota,
                             produto: Produto?,
                             quantProduto: Int,
                             usuario3: Usuario,
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
            this.usuario = usuario3
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

  private fun insertNota(bean: VO): Nota {
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
          if(usuarioDefault.admin) query
          else query.localizacao.startsWith(abreviacaoNota)
        }
    }

  abstract fun createVo(): VO
  //TODO fazer o createVo generico
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

  fun imprimir(itemNota: ItemNota?) = execString {
    val itens =
      ItemNota.where()
        .nota.eq(itemNota?.nota)
        .status.eq(statusImpressao)
        .order()
        .nota.loja.numero.asc()
        .nota.numero.asc()
        .findList()
    imprime(itens)
  }

  fun imprime() = execString {
    val itens =
      ItemNota.where()
        .let {q -> if(usuarioDefault.admin) q else q.impresso.eq(false)}
        .status.eq(statusImpressao)
        .order()
        .nota.loja.numero.asc()
        .nota.numero.asc()
        .findList()
    imprime(itens)
  }

  fun imprime(itens: List<ItemNota>) = execString {
    val etiquetas = Etiqueta.findByStatus(statusImpressao)
    //TODO Refatorar
    etiquetas.joinToString(separator = "\n") {etiqueta ->
      imprime(itens, etiqueta)
    }
  }

  private fun imprime(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.filter {it.abreviacao == abreviacaoDefault}
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
  private val notaProdutoSaci: List<NotaSaci>
    get() = if(entityVo == null) when(tipo) {
      SAIDA   -> Nota.findNotaSaidaSaci(numeroNF)
      ENTRADA -> Nota.findNotaEntradaSaci(numeroNF)
    }.filter {
      usuario.admin || (it.tipo != "PEDIDO_E")
    }
    else emptyList()
  val notaSaci
    get() = notaProdutoSaci.firstOrNull()
  val nota: Nota?
    get() = entityVo?.nota ?: when(tipo) {
      SAIDA   -> Nota.findSaida(numeroNF)
      ENTRADA -> Nota.findEntrada(numeroNF)
    }

  private fun atualizaNota() {
    if(!readOnly) if(entityVo == null) {
      val nota = notaSaci
      if(nota != null) {
        tipoNota = TipoNota.value(nota.tipo) ?: OUTROS_E
        rota = nota.rota
      }
      else {
        tipoNota = OUTROS_E
        rota = ""
      }
      produtos.clear()
      val produtosVo =
        notaProdutoSaci.filter {!it.isSave()}
          .flatMap {notaSaci ->
            val prd = Produto.findProduto(notaSaci.prdno, notaSaci.grade)
            val localizacoes =
              prd?.localizacoes()
                .orEmpty()
                .sorted()
            val ultimaLocalizacao = localizacoes.sorted().lastOrNull() ?: ""
            return@flatMap if(tipoNota.tipoMov == SAIDA) {
              var quant = notaSaci.quant ?: 0
              val produtosLocais =
                localizacoes.asSequence()
                  .map {localizacao ->
                    ProdutoVO(prd, tipoNota.tipoMov, LocProduto(localizacao)).apply {
                      val saldo = saldo
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
                  .toList()
              produtosLocais
            }
            else listOf(ProdutoVO(prd, tipoNota.tipoMov, LocProduto(ultimaLocalizacao)).apply {
              quantidade = notaSaci.quant ?: 0
            })
          }
      produtos.addAll(produtosVo.asSequence().filter {
        it.quantidade != 0 && it.codigo != "" && it.localizacao?.localizacao?.startsWith(abreviacaoNota) ?: false
      }.sortedWith(compareBy(ProdutoVO::codigo, ProdutoVO::grade, ProdutoVO::localizacao)).toList())
    }
  }

  val tipoNotaDescricao: String
    get() {
      return if(tipoNota == PEDIDO_E || tipoNota == PEDIDO_S) "Pedido $rota".trim()
      else tipoNota.descricao
    }
  val dataNota: LocalDate
    get() = toEntity()?.dataNota ?: notaSaci?.date?.localDate() ?: LocalDate.now()
  val lancamento: LocalDate
    get() = toEntity()?.nota?.lancamento ?: LocalDate.now()
  val horaLacamento: LocalDateTime
    get() = toEntity()?.nota?.run {LocalDateTime.of(lancamento, hora)} ?: LocalDateTime.now()
  val dataEmissao: LocalDate
    get() = toEntity()?.nota?.dataEmissao ?: notaSaci?.dt_emissao?.localDate() ?: LocalDate.now()
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
    get() = produto?.localizacoes()?.map {LocProduto(it)}.orEmpty()
  var status: StatusNota? = null
}

class ProdutoVO(val produto: Produto?, val tipoMov: TipoMov, var localizacao: LocProduto?) {
  val codigo: String = produto?.codigo ?: ""
  val grade: String = produto?.grade ?: ""
  var quantidade: Int = 0
  var selecionado: Boolean = false
  val saldoInsuficiente: Boolean
    get() = if(tipoMov == SAIDA) saldo < quantidade
    else false
  val saldo: Int
    get() = produto?.saldoLoja(localizacao?.localizacao) ?: 0
  val saldoFinal
    get() = saldo + if(tipoMov == SAIDA) -quantidade else quantidade
  val descricaoProduto: String
    get() = produto?.descricao ?: ""
  var value: ItemNota? = null
}
