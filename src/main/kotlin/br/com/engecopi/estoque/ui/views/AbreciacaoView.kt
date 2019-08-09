package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.viewmodel.AbreciacaoViewModel
import br.com.engecopi.framework.ui.view.LayoutView

class AbreciacaoView: LayoutView<AbreciacaoViewModel>() {

  init {
    viewModel = AbreciacaoViewModel(this)
  }

  override fun updateView() {
  }

  override fun updateModel() {
  }
}