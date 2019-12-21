package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EntregaFututaEditorViewModel(view: IEntregaFututaEditorView):
  NotaViewModel<EntregaFututaVo, IEntregaFututaEditorView>(view, SAIDA, ENTREGUE, ENTREGUE, "") {
  override fun newBean(): EntregaFututaVo {
    return EntregaFututaVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(VENDAF)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaFututaVo()
  
  fun notasConferidas(): List<EntregaFututaVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

interface IEntregaFututaEditorView: INotaView