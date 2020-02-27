package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.DEPOSITO
import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
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
import br.com.engecopi.estoque.viewmodel.movimentacao.ETipoGrupo.BLUE
import br.com.engecopi.estoque.viewmodel.movimentacao.ETipoGrupo.GREEN
import br.com.engecopi.estoque.viewmodel.movimentacao.ETipoGrupo.RED
import br.com.engecopi.estoque.viewmodel.movimentacao.ETipoGrupo.SELECT_FT
import br.com.engecopi.estoque.viewmodel.movimentacao.ETipoGrupo.WHITE
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.localDate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

abstract class NotaViewModel<VO: NotaVo, V: INotaView>(view: V,
                                                       val tipo: TipoMov,
                                                       private val statusDefault: StatusNota,
                                                       private val statusImpressao: StatusNota):
  CrudViewModel<ItemNota, QItemNota, VO, V>(view) {
  private val print = NotaPrint()
  
  override fun update(bean: VO) {
    if(bean.localizacao?.localizacao.isNullOrBlank()) throw EViewModelError("Não foi especificado a localização do item")
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
    val usuario = bean.usuario ?: usuarioDefault
    val addTime = LocalTime.now()
    if(bean.notaSaci == null) {
      val produto = saveProduto(bean.produto)
      if(Nota.itemDuplicado(nota, produto)) {
        val msg = "O produto ${produto.codigo} - ${produto.descricao}. Já foi inserido na nota ${nota.numero}."
        view.showWarning(msg)
      }
      else {
        bean.entityVo =
          insertItemNota(nota, produto, bean.quantProduto ?: 0, usuario, bean.localizacao?.localizacao, addTime)
      }
    }
    else {
      val produtos = bean.produtos.filter {it.selecionado && it.quantidade != 0}
      val produtosJaInserido =
        produtos.asSequence()
          .distinctBy {it.produto.id}
          .filter {prd ->
            prd.produto.let {Nota.itemDuplicado(nota, it)}
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
            if(usuario.temProduto(prd.produto)) bean.entityVo =
              insertItemNota(nota, prd.produto, prd.quantidade, usuario, prd.localizacao?.localizacao, addTime)
          }
        }
    }
  }
  
  private fun insertItemNota(nota: Nota,
                             produto: Produto?,
                             quantProduto: Int,
                             usuario: Usuario,
                             local: String?,
                             addTime: LocalTime): ItemNota? {
    if(local.isNullOrBlank()) throw EViewModelError("Não foi especificado a localização do item")
    val saldoLocal = produto?.saldoLocalizacao(local) ?: 0
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
            this.hora = addTime
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
    produto ?: throw EViewModelError("Produto não encontrado no saci")
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
      return QItemNota().setUseQueryCache(true)
        .fetch("nota")
        .fetch("usuario")
        .fetch("produto")
        .fetch("produto.vproduto")
        .fetch("produto.viewProdutoLoc")
        .nota.tipoMov.eq(tipo)
        .filtroTipoNota()
        .filtroStatus()
        .or()
        .nota.loja.eq(lojaDeposito)
        .nota.tipoNota.`in`(TipoNota.lojasExternas)
        .endOr()
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
      this.numeroBaixa = itemNota.numeroEntrega.joinToString(" ") {it.numero}
      this.dataBaixa = itemNota.nota?.dataBaixa()
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
    return nota.numero.startsWith(text)
      .codigoBarraCliente.startsWith(text)
      .and()
      .produto.viewProdutoLoc.localizacao.contains(text)
      .produto.viewProdutoLoc.loja.eq(lojaDeposito)
      .endAnd()
      .produto.vproduto.codigo.contains(" $text")
      .produto.vproduto.nome.contains(text)
  }
  
  override fun QItemNota.filterDate(date: LocalDate): QItemNota {
    return nota.data.eq(date)
  }
  
  abstract fun QItemNota.filtroStatus(): QItemNota
  
  abstract fun QItemNota.filtroTipoNota(): QItemNota
  
  override fun delete(bean: VO) {
    bean.toEntity()
      ?.also {item ->
        item.delete()
      }
  }
  
  fun findLojas(loja: Loja?): List<Loja> = execList {
    loja?.let {listOf(it)} ?: Loja.all()
  }
  
  fun imprimirItem(itemNota: ItemNota?) = execString {
    print.imprimirItem(itemNota, statusImpressao)
      .updateView()
  }
  
  fun imprimirNotaCompleta(itemNota: ItemNota?) = execString {
    print.imprimirNotaCompleta(itemNota, statusImpressao)
      .updateView()
  }
  
  fun imprimirNotaCompletaAgrupada(itemNota: ItemNota?) = execString {
    print.imprimirNotaCompletaAgrupada(itemNota, statusImpressao)
      .updateView()
  }
  
  fun imprimirItensPendentes() = execString {
    print.imprimirItensPendentes(statusImpressao)
      .updateView()
  }
  
  fun imprimirItens(itens: List<ItemNota>) = execString {
    print.imprimirItens(itens, statusImpressao)
      .updateView()
  }
  
  fun desfazOperacao(item: ItemNota?) = exec {
    item?.desfazerOperacao()
    view.updateView()
  }
}

abstract class NotaVo(val tipo: TipoMov, private val abreviacaoNota: String?): EntityVo<ItemNota>() {
  override fun findEntity(): ItemNota? {
    return ItemNota.find(nota, produto)
  }
  
  var usuario: Usuario? = null
  var numeroCodigo: String? = ""
  var numeroCodigoReduzido: String? = ""
  var numeroBaixa: String = ""
  var dataBaixa: LocalDate? = null
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
  private val mapNotaSaci = mutableMapOf<String?, List<NotaProdutoSaci>>()
  private val notaProdutoProdutoSaci: List<NotaProdutoSaci>
    get() = if(entityVo == null) mapNotaSaci.getOrPut(numeroNF) {
      when(tipo) {
        SAIDA   -> Nota.findNotaSaidaSaci(lojaDeposito, numeroNF).filter {
          val user = usuario ?: return@filter false
          user.admin || (it.tipo != "PEDIDO_E")
        }
        ENTRADA -> Nota.findNotaEntradaSaci(lojaDeposito, numeroNF)
      }
    }
    else emptyList()
  val notaSaci
    get() = notaProdutoProdutoSaci.firstOrNull()
  val nota: Nota?
    get() = entityVo?.nota ?: Nota.findNota(lojaDeposito, numeroNF, tipo)
  
  fun atualizaNota() {
    if(!readOnly && entityVo == null) {
      val nota = notaSaci ?: return
      tipoNota = TipoNota.value(nota.tipo) ?: OUTROS_E
      rota = nota.rota
      produtos.clear()
      val produtosVo = notaProdutoProdutoSaci.flatMap {notaSaci ->
        if(tipoNota.tipoMov == SAIDA) listItensSaida(notaSaci)
        else listItensEntrada(notaSaci)
      }
      produtos.addAll(produtosVo.asSequence().filter {
        it.quantidade != 0 && it.codigo != "" && it.localizacao?.localizacao?.startsWith(abreviacaoNota ?: "") ?: false
      }.sortedWith(compareBy(ProdutoVO::isSave, ProdutoVO::codigo, ProdutoVO::grade, ProdutoVO::localizacao)).toList())
    }
  }
  
  private fun listItensEntrada(notaProdutoSaci: NotaProdutoSaci): List<ProdutoVO> {
    val prd = Produto.findProduto(notaProdutoSaci.prdno, notaProdutoSaci.grade) ?: return emptyList()
    val localizacoes =
      prd.localizacoes(abreviacaoNota)
        .sorted()
    val ultimaLocalizacao = localizacoes.max() ?: ""
    val produtoVo = ProdutoVO(prd, RECEBIDO, LocProduto(ultimaLocalizacao), notaProdutoSaci.isSave()).apply {
      quantidade = notaProdutoSaci.quant ?: 0
    }
    return listOf(produtoVo)
  }
  
  private fun listItensSaida(notaProdutoSaci: NotaProdutoSaci): List<ProdutoVO> {
    val prd = Produto.findProduto(notaProdutoSaci.prdno, notaProdutoSaci.grade) ?: return emptyList()
    var quant = notaProdutoSaci.quant ?: return emptyList()
    val localizacoes =
      prd.localizacoes(abreviacaoNota)
        .sorted()
    val ultimaLocalizacao = localizacoes.max() ?: ""
    val produtosLocais = localizacoes.map {localizacao ->
      ProdutoVO(prd, CONFERIDA, LocProduto(localizacao), notaProdutoSaci.isSave()).apply {
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
  
  fun produtosCompletos(): Boolean {
    return produtos.all {it.isSave}
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
      val nota = notaProdutoProdutoSaci
      val produtos = if(nota.isNotEmpty()) nota.asSequence().mapNotNull {notaSaci ->
        Produto.findProduto(notaSaci.prdno, notaSaci.grade)
      }.filter {produto ->
        val user = usuario ?: return@filter false
        user.temProduto(produto)
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
      quantProduto = toEntity()?.quantidade ?: notaProdutoProdutoSaci.firstOrNull {neSaci ->
        value?.let {produto ->
          neSaci.chaveProdutoGrade == produto.chaveProdutoGrade
        } ?: false
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
    get() = produto?.saldoLocalizacao(localizacao?.localizacao) ?: 0
  var localizacao: LocProduto? = null
  val abreviacao: String
    get() = localizacao?.abreviacao ?: ""
  val localizacaoProduto
    get() = produto?.localizacoes(abreviacaoNota)?.map {LocProduto(it)}.orEmpty()
  var status: StatusNota? = null
}

class ProdutoVO(val produto: Produto, val statusNota: StatusNota, var localizacao: LocProduto?, var isSave: Boolean) {
  val codigo: String = produto.codigo
  val grade: String = produto.grade
  var quantidade: Int = 0
  var selecionado: Boolean = false
  val saldo: Int
    get() = produto.saldoLocalizacao(localizacao?.localizacao) - if(isSave) quantidade * multipicador else 0
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
    grupoSelecao = when {
      selecionado    -> when {
        first -> SELECT_FT
        else  -> BLUE
      }
      saldoFinal < 0 -> RED
      !allowSelect() -> GREEN
      else           -> WHITE
    }
  }
}

enum class ETipoGrupo(val ordem: Int) {
  SELECT_FT(0),
  RED(1),
  WHITE(2),
  BLUE(3),
  GREEN(4)
}

interface INotaView: ICrudView