package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_R
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EntregaClienteEditorAbastecimentoViewModel(view: IEntregaClienteEditorAbastecimentoView):
  NotaViewModel<EntregaClienteAbastecimentoVo, IEntregaClienteEditorAbastecimentoView>(view,
                                                                                       SAIDA,
                                                                                       ENTREGUE,
                                                                                       ENTREGUE) {
  override fun newBean(): EntregaClienteAbastecimentoVo {
    return EntregaClienteAbastecimentoVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.notIn(VENDAF, PEDIDO_R)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaClienteAbastecimentoVo()
  
  fun notasConferidas(): List<EntregaClienteAbastecimentoVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

interface IEntregaClienteEditorAbastecimentoView: INotaView