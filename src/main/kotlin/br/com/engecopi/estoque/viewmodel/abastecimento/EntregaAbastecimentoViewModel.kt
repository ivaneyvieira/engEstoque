package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_A
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaAbastecimentoViewModel(view: IEntregaAbastecimentoView) : NotaViewModel<EntregaAbastecimentoVo, IEntregaAbastecimentoView>(
  view,
  SAIDA,
  ENTREGUE,
  CONFERIDA) {
  private val find = EntregaAbastecimentoFind(view)

  override fun newBean(): EntregaAbastecimentoVo {
    return EntregaAbastecimentoVo()
  }

  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(PEDIDO_A)
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA).nota.usuario.isNotNull.nota.sequencia.ne(0)
  }

  override fun createVo() = EntregaAbastecimentoVo()

  fun findKey(key: String) = exec {
    find.findKey(key).updateView()
  }

  fun notasConferidas(): List<ItemNota> {
    return QItemNota().status.eq(CONFERIDA).findList()
  }
}

class EntregaAbastecimentoVo : NotaVo(SAIDA, "")

interface IEntregaAbastecimentoView : INotaView

