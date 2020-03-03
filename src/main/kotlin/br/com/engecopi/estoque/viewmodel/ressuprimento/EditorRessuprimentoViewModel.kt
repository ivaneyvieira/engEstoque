package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.LancamentoOrigem.RESSUPRI
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EditorRessuprimentoViewModel(ressuprimentoView: IEditorRessuprimentoView):
  NotaViewModel<EntregaRessuprimentoVo, IEditorRessuprimentoView>(ressuprimentoView, SAIDA, ENTREGUE, ENTREGUE) {
  override fun newBean(): EntregaRessuprimentoVo {
    return EntregaRessuprimentoVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.lancamentoOrigem.eq(RESSUPRI)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaRessuprimentoVo()
  
  fun notasConferidas(): List<EntregaRessuprimentoVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

interface IEditorRessuprimentoView: INotaView