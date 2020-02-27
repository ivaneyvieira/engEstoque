package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaClienteViewModel(view: IEntregaClienteView):
  NotaViewModel<EntregaClienteVo, IEntregaClienteView>(view, SAIDA, ENTREGUE, CONFERIDA) {
  private val find = EntregaClienteFind(view)
  
  override fun newBean(): EntregaClienteVo {
    return EntregaClienteVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.notIn(TipoNota.lojasExternas)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaClienteVo()
  
  fun findKey(key: String) = exec {
    find.findKey(key)
      .updateView()
  }
  
  fun notasConferidas(): List<EntregaClienteVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

class EntregaClienteVo: NotaVo(SAIDA, "")

interface IEntregaClienteView: INotaView

