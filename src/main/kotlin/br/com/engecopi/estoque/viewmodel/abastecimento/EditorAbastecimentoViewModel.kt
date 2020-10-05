package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_A
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EditorAbastecimentoViewModel(view: IEditorAbastecimentoView):
  NotaViewModel<EntregaAbastecimentoVo, IEditorAbastecimentoView>(view,
                                                                  SAIDA,
                                                                  ENTREGUE,
                                                                  ENTREGUE) {
  override fun newBean(): EntregaAbastecimentoVo {
    return EntregaAbastecimentoVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(PEDIDO_A)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaAbastecimentoVo()
  
  fun notasConferidas(): List<ItemNota> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
  }
}

interface IEditorAbastecimentoView: INotaView