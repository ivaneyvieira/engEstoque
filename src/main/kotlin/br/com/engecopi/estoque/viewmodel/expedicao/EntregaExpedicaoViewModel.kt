package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaExpedicaoViewModel(view: IEntregaExpedicaoView):
  NotaViewModel<EntregaExpedicaoVo, IEntregaExpedicaoView>(view, SAIDA, ENTREGUE, CONFERIDA) {
  private val find = EntregaExpedicaoFind(view)
  
  override fun newBean(): EntregaExpedicaoVo {
    return EntregaExpedicaoVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.lancamentoOrigem.eq(EXPEDICAO)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaExpedicaoVo()
  
  fun findKey(key: String) = exec {
    find.findKey(key)
      .updateView()
  }
  
  fun notasConferidas(): List<EntregaExpedicaoVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

class EntregaExpedicaoVo: NotaVo(SAIDA, "")

interface IEntregaExpedicaoView: INotaView

