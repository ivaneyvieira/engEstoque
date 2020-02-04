package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_R
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.saci.beans.NotaProdutoSaci

class PedidoRessuprimentoViewModel(view: IPedidoRessuprimentoView):
  NotaViewModel<EntregaRessuprimentoVo, IPedidoRessuprimentoView>(view = view,
                                                                  tipo = SAIDA,
                                                                  statusDefault = INCLUIDA,
                                                                  statusImpressao = INCLUIDA) {
  private val processing = NFRessuprimentoProcessamento()
  private val print = NFRessuprimentoPrint()
  private val find = RessuprimentoFind(view)
  
  fun processaKey(notasSaci: List<ItemRessuprimento>) = exec {
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
  
  override fun createVo(): EntregaRessuprimentoVo {
    return EntregaRessuprimentoVo()
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(INCLUIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(PEDIDO_R)
  }
  
  override fun newBean(): EntregaRessuprimentoVo {
    return EntregaRessuprimentoVo()
  }
  
  fun findKey(key: String) = exec {
    find.findKey(key)
  }
}

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

interface IPedidoRessuprimentoView: INotaView {
  fun updateGrid()
}


