package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_R
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaClienteAbastecimentoViewModel(view: IEntregaClienteAbastecimentoView):
  NotaViewModel<EntregaClienteAbastecimentoVo, IEntregaClienteAbastecimentoView>(view, SAIDA, ENTREGUE, CONFERIDA) {
  private val find = EntregaClienteAbastecimentoFind(view)
  
  override fun newBean(): EntregaClienteAbastecimentoVo {
    return EntregaClienteAbastecimentoVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.notIn(VENDAF, PEDIDO_R)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = EntregaClienteAbastecimentoVo()
  
  fun findKey(key: String) = exec {
    find.findKey(key)
      .updateView()
  }
  
  fun notasConferidas(): List<EntregaClienteAbastecimentoVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

class EntregaClienteAbastecimentoVo: NotaVo(SAIDA, "")

interface IEntregaClienteAbastecimentoView: INotaView

