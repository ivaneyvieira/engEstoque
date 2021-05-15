package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.StatusNota.*
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EditorExpedicaoViewModel(expedicaoView: IEditorExpedicaoView) : NotaViewModel<EntregaExpedicaoVo, IEditorExpedicaoView>(
  expedicaoView,
  SAIDA,
  ENTREGUE,
  ENTREGUE) {
  override fun newBean(): EntregaExpedicaoVo {
    return EntregaExpedicaoVo()
  }

  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.notIn(TipoNota.lojasExternas)
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA).nota.usuario.isNotNull.nota.sequencia.ne(0)
  }

  override fun createVo() = EntregaExpedicaoVo()

  fun notasConferidas(): List<ItemNota> {
    return QItemNota().status.eq(CONFERIDA).findList()
  }
}

interface IEditorExpedicaoView : INotaView