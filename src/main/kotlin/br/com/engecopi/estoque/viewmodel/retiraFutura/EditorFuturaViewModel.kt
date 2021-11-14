package br.com.engecopi.estoque.viewmodel.retiraFutura

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.StatusNota.*
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EditorFuturaViewModel(view: IEditorFuturaView):
  NotaViewModel<RetiraFututaVo, IEditorFuturaView>(view, SAIDA, ENTREGUE, ENTREGUE) {
  override fun newBean(): RetiraFututaVo {
    return RetiraFututaVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(VENDAF)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = RetiraFututaVo()
  
  fun notasConferidas(): List<ItemNota> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
  }
}

interface IEditorFuturaView: INotaView