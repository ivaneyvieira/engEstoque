package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel

class EntregaClienteEditorViewModel(view: IEntregaClienteEditorView):
  NotaViewModel<EntregaClienteVo, IEntregaClienteEditorView>(view, SAIDA, ENTREGUE, ENTREGUE) {
  override fun newBean(): EntregaClienteVo {
    return EntregaClienteVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.notIn(TipoNota.lojasExternas)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, ENT_LOJA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
      .nota.lancamentoOrigem.eq(EXPEDICAO)
  }
  
  override fun createVo() = EntregaClienteVo()
  
  fun notasConferidas(): List<EntregaClienteVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

interface IEntregaClienteEditorView: INotaView