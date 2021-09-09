package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_R
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaRessuprimentoViewModel(view: IEntregaRessuprimentoView) :
        NotaViewModel<EntregaRessuprimentoVo, IEntregaRessuprimentoView>(view,
                                                                         SAIDA,
                                                                         statusDefault = ENTREGUE,
                                                                         statusImpressao = CONFERIDA) {
  private val find = EntregaRessuprimentoFind(view)

  override fun newBean(): EntregaRessuprimentoVo {
    return EntregaRessuprimentoVo()
  }

  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(PEDIDO_R)
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA).nota.usuario.isNotNull.nota.sequencia.ne(0)
  }

  override fun createVo() = EntregaRessuprimentoVo()

  fun findKey(key: String) = exec {
    find.findKey(key).updateView()
  }

  fun notasConferidas() = find.notasConferidas()
}

class EntregaRessuprimentoVo : NotaVo(SAIDA, "")

interface IEntregaRessuprimentoView : INotaView

