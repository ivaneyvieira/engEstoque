package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.StatusNota.RECEBIDO
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.framework.viewmodel.IView

class EntradaViewModel(view: IView) : NotaViewModel<EntradaVo>(view, ENTRADA, RECEBIDO, RECEBIDO,
                                                               abreviacaoDefault) {
  override fun newBean(): EntradaVo {
    return EntradaVo()
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.eq(RECEBIDO)
  }

  override fun createVo() = EntradaVo()
}

class EntradaVo : NotaVo(ENTRADA, abreviacaoDefault)
