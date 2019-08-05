package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel

class PainelGeralModel(view: IView) : ViewModel(view) {
  fun listSaidaCancel(): List<Nota> {

    return Nota.listSaidaCancel()
  }
}