package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.StatusNota.RECEBIDO
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.query.QItemNota

class EntradaViewModel(view: IEntradaView): NotaViewModel<EntradaVo, IEntradaView>(view, ENTRADA, RECEBIDO,
                                                                                         RECEBIDO,
                                                                                         abreviacaoDefault) {
  override fun newBean(): EntradaVo {
    return EntradaVo()
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.eq(RECEBIDO)
  }

  override fun createVo() = EntradaVo()
}

class EntradaVo: NotaVo(ENTRADA, abreviacaoDefault)

interface IEntradaView: INotaView {
}
