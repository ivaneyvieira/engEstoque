package br.com.engecopi.framework.ui.view

import br.com.engecopi.framework.viewmodel.IView

abstract class ViewEmpty: IView {
  abstract override fun showWarning(msg: String)
  
  abstract override fun showError(msg: String)
  
  abstract override fun showInfo(msg: String)
}
