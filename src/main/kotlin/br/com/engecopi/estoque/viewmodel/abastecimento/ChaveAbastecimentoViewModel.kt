package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.LancamentoOrigem.ABASTECI
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.ViewPedidoAbastecimento
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.model.query.QViewPedidoAbastecimento
import br.com.engecopi.estoque.ui.log
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView
import br.com.engecopi.saci.beans.NotaProdutoSaci
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ChaveAbastecimentoViewModel(view: IAbastecimentoView):
  CrudViewModel<ViewPedidoAbastecimento, QViewPedidoAbastecimento, AbastecimentoVo, IAbastecimentoView>(view) {
  private val print = ChaveAbastecimentoPrint()
  private val processing = ChaveAbastecimentoProcessamento()
  private val find = ChaveAbastecimentoFind()
  
  override fun newBean(): AbastecimentoVo {
    return AbastecimentoVo()
  }
  
  override fun update(bean: AbastecimentoVo) {
    log?.error("Atualização não permitida")
  }
  
  override fun add(bean: AbastecimentoVo) {
    log?.error("Inserssão não permitida")
  }
  
  override fun delete(bean: AbastecimentoVo) {
    val nota = bean.findEntity() ?: return
    val saida = Nota.findSaida(nota.loja, nota.numero) ?: return
    
    QItemNota().nota.equalTo(saida)
      .status.notIn(ENTREGUE, ENT_LOJA)
      .localizacao.startsWith(bean.abreviacao)
      .delete()
    
    if(saida.itensNota().isEmpty())
      saida.delete()
  }
  
  override val query: QViewPedidoAbastecimento
    get() = QViewPedidoAbastecimento().loja.eq(lojaDeposito).nota.lancamentoOrigem.eq(ABASTECI)
  
  private fun QViewPedidoAbastecimento.filtroNotaSerie(): QViewPedidoAbastecimento {
    val tipos = usuarioDefault.series.map {it.tipoNota}
    val queryOr = or()
    val querySeries = tipos.fold(queryOr) {q, tipo ->
      q.nota.tipoNota.eq(tipo)
    }
    
    return querySeries.endOr()
  }
  
  override fun QViewPedidoAbastecimento.orderQuery(): QViewPedidoAbastecimento {
    return this.order()
      .lancamento.desc()
      .id.desc()
  }
  
  override fun ViewPedidoAbastecimento.toVO(): AbastecimentoVo {
    val bean = this
    return AbastecimentoVo().apply {
      numero = bean.numero
      tipoMov = bean.tipoMov
      tipoNota = bean.tipoNota
      rota = bean.rota
      fornecedor = bean.fornecedor
      cliente = bean.cliente
      data = bean.data
      dataEmissao = bean.dataEmissao
      lancamento = bean.lancamento
      hora = bean.hora
      observacao = bean.observacao
      loja = bean.loja
      sequencia = bean.sequencia
      usuario = bean.usuario
      abreviacao = bean.abreviacao
    }
  }
  
  fun processaKey(notasSaci: List<ItemAbastecimento>) = exec {
    processing.processaKey(notasSaci)
      .updateView()
  }
  
  fun imprimeTudo() = execString {
    print.imprimeTudo()
      .updateView()
  }
  
  fun imprimir(nota: Nota?) = execList {
    print.imprimir(nota)
      .updateView()
  }
  
  fun findNotaSaidaKey(key: String) = execList {
    find.findNotaSaidaKey(key)
  }
  
  fun findLoja(storeno: Int?): Loja? = Loja.findLoja(storeno)
  
  fun abreviacoes(prdno: String?, grade: String?): List<String> {
    val produto = Produto.findProduto(prdno, grade) ?: return emptyList()
    return ViewProdutoLoc.abreviacoesProduto(produto)
  }
  
  override fun QViewPedidoAbastecimento.filterString(text: String): QViewPedidoAbastecimento {
    return nota.numero.startsWith(text)
  }
  
  override fun QViewPedidoAbastecimento.filterDate(date: LocalDate): QViewPedidoAbastecimento {
    return data.eq(date)
  }
  
  fun saldoProduto(notaProdutoSaci: NotaProdutoSaci, abreviacao: String): Int {
    val produto = Produto.findProduto(notaProdutoSaci.codigo(), notaProdutoSaci.grade)
    return produto?.saldoAbreviacao(abreviacao) ?: 0
  }
  
  fun processaVendas(venda: VendasCaixa) {
    val produto = Produto.findProduto(venda.prdno, venda.grade) ?: return
    val locacalizacoes = produto.viewProdutoLoc ?: return
    locacalizacoes.filter {it.abreviacao == "S"}
  }
}

class AbastecimentoVo: EntityVo<ViewPedidoAbastecimento>() {
  override fun findEntity(): ViewPedidoAbastecimento? {
    return ViewPedidoAbastecimento.findSaida(lojaDeposito, numero, abreviacao)
  }
  
  var numero: String = ""
  var tipoMov: TipoMov = ENTRADA
  var tipoNota: TipoNota? = null
  var rota: String = ""
  var fornecedor: String = ""
  var cliente: String = ""
  var data: LocalDate = LocalDate.now()
  var dataEmissao: LocalDate = LocalDate.now()
  var lancamento: LocalDate = LocalDate.now()
  var hora: LocalTime = LocalTime.now()
  var observacao: String = ""
  var loja: Loja? = null
  var sequencia: Int = 0
  var usuario: Usuario? = null
  var abreviacao: String? = ""
  var impresso: Boolean = false
  val dataHoraLancamento
    get() = LocalDateTime.of(data, hora)
}

data class ItemAbastecimento(val notaProdutoSaci: NotaProdutoSaci,
                             val saldo: Int,
                             val abrevicao: String,
                             var selecionado: Boolean = false) {
  val prdno = notaProdutoSaci.prdno
  val grade = notaProdutoSaci.grade
  val nome = notaProdutoSaci.nome
  val quant = notaProdutoSaci.quant ?: 0
  val saldoFinal = saldo - quant
  
  fun isSave() = notaProdutoSaci.isSave()
}

interface IAbastecimentoView: ICrudView

