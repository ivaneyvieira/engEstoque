package br.com.engecopi.estoque.viewmodel.retiraFutura

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.RETIRAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class RetiraFututaViewModel(view: IRetiraFuturaView) : NotaViewModel<RetiraFututaVo, IRetiraFuturaView>(
  view, tipo = SAIDA, statusDefault = ENTREGUE, statusImpressao = CONFERIDA
) {
  private val find = RetiraFuturaFind(view)

  override fun newBean(): RetiraFututaVo {
    return RetiraFututaVo()
  }

  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(RETIRAF)
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA).nota.usuario.isNotNull.nota.sequencia.ne(0)
  }

  override fun createVo() = RetiraFututaVo()

  fun findKey(key: String) = exec {
    find.findKey(key).updateView()
  }

  fun notasConferidas() = find.notasConferidas()
}

class RetiraFututaVo : NotaVo(SAIDA, "")

interface IRetiraFuturaView : INotaView

