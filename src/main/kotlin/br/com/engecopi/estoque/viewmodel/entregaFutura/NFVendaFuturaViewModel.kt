package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.ViewNotaFutura
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.model.query.QViewNotaFutura
import br.com.engecopi.estoque.ui.log
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENotaNaoEntregaFutura
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.mid
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class NFVendaFuturaViewModel(view: INFVendaFuturaView):
  CrudViewModel<ViewNotaFutura, QViewNotaFutura, NFVendaFuturaVo, INFVendaFuturaView>(view) {
  private val processing = NFVendaFututraProcessamento(view)
  private val print = NFVendaFuturaPrint(view)
  
  override fun newBean(): NFVendaFuturaVo {
    return NFVendaFuturaVo()
  }
  
  override fun update(bean: NFVendaFuturaVo) {
    log?.error("Atualização não permitida")
  }
  
  override fun add(bean: NFVendaFuturaVo) {
    log?.error("Inserssão não permitida")
  }
  
  override fun delete(bean: NFVendaFuturaVo) {
    val nota = bean.findEntity() ?: return
    val saida = Nota.findSaida(nota.loja, nota.numero) ?: return
  
    QItemNota().nota.equalTo(saida)
      .localizacao.startsWith(bean.abreviacao)
      .delete()
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
  
  override fun ViewNotaFutura.toVO(): NFVendaFuturaVo {
    val bean = this
    return NFVendaFuturaVo().apply {
      numero = bean.numero
      numeroBaixa = bean.numeroBaixa ?: ""
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
  
  fun processaKey(notasSaci: List<ItemVendaFutura>) = execValue {
    val nota = processing.processaKey(notasSaci)
    crudBean =
      ViewNotaFutura.findNotaFutura(nota)
        ?.toVO()
    view.updateView()
    return@execValue nota
  }
  
  fun imprimeTudo() = execString {print.imprimeTudo()}
  
  fun imprimir(nota: Nota?) = execString {print.imprimir(nota)}
  
  fun findNotaSaidaKey(key: String) = execList {
    val storeno =
      key.mid(0, 1)
        .toIntOrNull()
    val nfno = key.mid(1)
    val notaSaci =
      Nota.findNotaSaidaSaci(storeno, nfno)
        .filter {ns ->
          when {
            usuarioDefault.isEstoqueVendaFutura -> ViewProdutoLoc.filtraLoc(ns.prdno, ns.grade)
            else                                -> true
          }
        }
    val numero = notaSaci.firstOrNull()?.numero ?: ""
    val ret = when {
      notaSaci.isEmpty()                           -> throw EChaveNaoEncontrada()
      notaSaci.firstOrNull()?.tipoNota() != VENDAF -> throw ENotaNaoEntregaFutura(numero)
      else                                         -> if(usuarioDefault.isEstoqueVendaFutura) {
        val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
        val notaSerie = nota.notaSerie() ?: throw EChaveNaoEncontrada()
        val tipo = notaSerie.tipoNota
        when {
          usuarioDefault.isTipoCompativel(tipo) -> notaSaci
          else                                  -> throw EViewModelError("O usuário não está habilitado para lançar esse tipo de nota (${notaSerie.descricao})")
        }
      }
      else notaSaci
    }
    view.updateView()
    ret
  }
  
  fun NotaProdutoSaci.notaSerie(): NotaSerie? {
    val tipo = TipoNota.value(tipo)
    return NotaSerie.findByTipo(tipo)
  }
  
  fun findLoja(storeno: Int?): Loja? = Loja.findLoja(storeno)
  
  fun abreviacoes(prdno: String?, grade: String?): List<String> {
    val produto = Produto.findProduto(prdno, grade) ?: return emptyList()
    return ViewProdutoLoc.abreviacoesProduto(produto)
  }
  
  override fun QViewNotaFutura.filterString(text: String): QViewNotaFutura {
    return nota.numero.startsWith(text)
  }
  
  override fun QViewNotaFutura.filterDate(date: LocalDate): QViewNotaFutura {
    return data.eq(date)
  }
  
  fun saldoProduto(notaProdutoSaci: NotaProdutoSaci, abreviacao: String): Int {
    val produto = Produto.findProduto(notaProdutoSaci.codigo(), notaProdutoSaci.grade)
    return produto?.saldoAbreviacao(lojaDeposito, abreviacao) ?: 0
  }
  
  fun processaVendas(venda: VendasCaixa) {
    val produto = Produto.findProduto(venda.prdno, venda.grade) ?: return
    val locacalizacoes = produto.viewProdutoLoc ?: return
    locacalizacoes.filter {it.abreviacao == "S"}
  }
}

class NFVendaFuturaVo: EntityVo<ViewNotaFutura>() {
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

data class ItemVendaFutura(val notaProdutoSaci: NotaProdutoSaci,
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

interface INFVendaFuturaView: ICrudView


