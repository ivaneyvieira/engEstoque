package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.ViewPedidoRessuprimento
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.model.query.QViewPedidoRessuprimento
import br.com.engecopi.estoque.ui.log
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.ICrudView
import br.com.engecopi.saci.beans.NotaProdutoSaci
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PedidoRessuprimentoViewModel(view: IPedidoRessuprimentoView):
  CrudViewModel<ViewPedidoRessuprimento, QViewPedidoRessuprimento, PedidoRessuprimentoVo, IPedidoRessuprimentoView>(view) {
  private val processing = RessuprimentoProcessamento(view)
  private val print = NFRessuprimentoPrint()
  private val find = RessuprimentoFind(view)
  
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
  
  override fun newBean(): PedidoRessuprimentoVo {
    return PedidoRessuprimentoVo()
  }
  
  fun processaKey(notasSaci: List<ItemRessuprimento>) = exec {
    processing.processaKey(notasSaci)
      .updateView()
  }
  
  override fun countQuery(): Int {
    return findQuery().size
  }
  
  override fun update(bean: PedidoRessuprimentoVo) {
    log?.error("Atualização não permitida")
  }
  
  override fun add(bean: PedidoRessuprimentoVo) {
    log?.error("Inserssão não permitida")
  }
  
  override fun delete(bean: PedidoRessuprimentoVo) {
    val nota = bean.findEntity() ?: return
    val saida = Nota.findSaida(nota.loja, nota.numero) ?: return
    
    QItemNota().nota.equalTo(saida)
      .status.notIn(ENTREGUE, ENT_LOJA)
      .localizacao.startsWith(bean.abreviacao)
      .delete()
    
    if(saida.itensNota().isEmpty())
      saida.delete()
  }
  
  override val query: QViewPedidoRessuprimento
    get() = QViewPedidoRessuprimento()
  
  override fun ViewPedidoRessuprimento.toVO(): PedidoRessuprimentoVo {
    val bean = this
    return PedidoRessuprimentoVo().apply {
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
}

class PedidoRessuprimentoVo: EntityVo<ViewPedidoRessuprimento>() {
  override fun findEntity(): ViewPedidoRessuprimento? {
    return ViewPedidoRessuprimento.findSaida(RegistryUserInfo.lojaDeposito, numero, abreviacao)
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

data class ChaveGroup(val nota: Nota?, val abreviacao: String?)

data class ItemRessuprimento(val notaProdutoSaci: NotaProdutoSaci,
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

interface IPedidoRessuprimentoView: ICrudView {
  fun updateGrid()
}


