package br.com.engecopi.estoque.viewmodel.retiraFutura

import br.com.engecopi.estoque.model.*
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.model.query.QViewNotaFutura
import br.com.engecopi.estoque.ui.log
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView
import br.com.engecopi.saci.beans.NotaProdutoSaci
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ChaveFuturaViewModel(view: IChaveFuturaView):
  CrudViewModel<ViewNotaFutura, QViewNotaFutura, ChaveFuturaVo, IChaveFuturaView>(view) {
  private val processing = ChaveFuturaProcessamento()
  private val print = ChaveFuturaPrint()
  private val find = ChaveFuturaFind()
  
  override fun newBean(): ChaveFuturaVo {
    return ChaveFuturaVo()
  }
  
  override fun update(bean: ChaveFuturaVo) {
    log?.error("Atualização não permitida")
  }
  
  override fun add(bean: ChaveFuturaVo) {
    log?.error("Inserssão não permitida")
  }
  
  override fun delete(bean: ChaveFuturaVo) {
    val nota = bean.findEntity() ?: return
    val saida = Nota.findSaida(nota.loja, nota.numero) ?: return
    
    QItemNota().nota.equalTo(saida)
      .status.notIn(ENTREGUE, ENT_LOJA)
      .localizacao.startsWith(bean.abreviacao)
      .delete()
    
    if(saida.itensNota().isEmpty())
      saida.delete()
  }
  
  override val query: QViewNotaFutura
    get() = QViewNotaFutura().nota.tipoNota.eq(VENDAF)
  
  private fun QViewNotaFutura.filtroNotaSerie(): QViewNotaFutura {
    val tipos = usuarioDefault.series.map {
      it.tipoNota
    }
    val queryOr = or()
    val querySeries = tipos.fold(queryOr) {q, tipo ->
      q.nota.tipoNota.eq(tipo)
    }
    return querySeries.endOr()
  }
  
  override fun QViewNotaFutura.orderQuery(): QViewNotaFutura {
    return this.order()
      .lancamento.desc()
      .id.desc()
  }
  
  override fun ViewNotaFutura.toVO(): ChaveFuturaVo {
    val bean = this
    return ChaveFuturaVo().apply {
      numero = bean.numero
      numeroBaixa = bean.numeroBaixa.filter {
        it.abreviacoes.contains(bean.abreviacao)
      }
        .joinToString(" ") {it.numero}
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
  
  override fun QViewNotaFutura.filterString(text: String): QViewNotaFutura {
    return nota.numero.startsWith(text)
  }
  
  override fun QViewNotaFutura.filterDate(date: LocalDate): QViewNotaFutura {
    return data.eq(date)
  }
  
  fun processaKey(notasSaci: List<ItemChaveFutura>) = exec {
    processing.processaKey(notasSaci)
      .updateView()
  }
  
  fun imprimeTudo() = execString {
    print.imprimeTudo()
      .updateView()
  }
  
  fun imprimir(nota: Nota?) = execString {
    print.imprimir(nota)
      .updateView()
  }
  
  fun findNotaSaidaKey(key: String) = execList {
    find.findNotaSaidaKey(key)
      .updateView()
  }
  
  fun findLoja(storeno: Int?) = find.findLoja(storeno)
  
  fun abreviacoes(prdno: String?, grade: String?) = find.abreviacoes(prdno, grade)
  
  fun saldoProduto(notaProdutoSaci: NotaProdutoSaci, abreviacao: String) =
    find.saldoProduto(notaProdutoSaci, abreviacao)
  
  fun processaVendas(venda: VendasCaixa) {
    val produto = Produto.findProduto(venda.prdno, venda.grade) ?: return
    val locacalizacoes = produto.viewProdutoLoc ?: return
    locacalizacoes.filter {it.abreviacao == "S"}
  }
}

class ChaveFuturaVo: EntityVo<ViewNotaFutura>() {
  override fun findEntity(): ViewNotaFutura? {
    return ViewNotaFutura.findSaida(loja, numero, abreviacao)
  }
  
  var numero: String = ""
  var numeroBaixa: String = ""
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

data class ItemChaveFutura(val notaProdutoSaci: NotaProdutoSaci,
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

interface IChaveFuturaView: ICrudView


