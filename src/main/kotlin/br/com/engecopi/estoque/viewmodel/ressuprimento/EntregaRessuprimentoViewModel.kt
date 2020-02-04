package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaRessuprimentoViewModel(view: IRessuprimentoView):
  NotaViewModel<RessuprimentoVo, IRessuprimentoView>(view, SAIDA, ENTREGUE, CONFERIDA, "") {
  private val find = EntregaRessuprimentoFind(view)
  
  override fun newBean(): RessuprimentoVo {
    return RessuprimentoVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(VENDAF)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
  }
  
  override fun createVo() = RessuprimentoVo()
  
  fun findKey(key: String) = exec {
    find.findKey(key)
      .updateView()
  }
  
  fun notasConferidas() = find.notasConferidas().map {it.toVO()}
}

class RessuprimentoVo: NotaVo(SAIDA, "")

interface IRessuprimentoView: INotaView

