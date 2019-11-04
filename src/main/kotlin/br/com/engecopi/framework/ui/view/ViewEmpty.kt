package br.com.engecopi.framework.ui.view

import br.com.engecopi.framework.viewmodel.IView

class ViewEmpty: IView {


  override fun showWarning(msg: String) {  }

  override fun showError(msg: String) {  }

  override fun showInfo(msg: String) {  }
}
