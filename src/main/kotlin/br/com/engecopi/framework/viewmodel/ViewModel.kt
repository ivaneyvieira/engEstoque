package br.com.engecopi.framework.viewmodel

import br.com.engecopi.framework.model.Transaction

abstract class ViewModel(val view: IView) {
  private var inTransaction = false

  private fun updateView(exception: EViewModel? = null) {
    exception?.message?.let {message ->
      view.showError(message)
    }
    view.updateView()
  }

  private fun updateModel() {
    view.updateModel()
  }

  @Throws(EViewModel::class)
  fun exec(block: () -> Unit) {
    try {
      if(inTransaction) block()
      else transaction {
        inTransaction = true
        updateModel()
        block()
        updateView()
        inTransaction = false
      }
    } catch(e: EViewModel) {
      updateView(e)
    } finally {
      inTransaction = false
    }
  }

  @Throws(EViewModel::class)
  fun <T> execValue(block: () -> T): T? {
    var ret: T? = null
    if(inTransaction) ret = block()
    else transaction {
      try {
        inTransaction = true
        updateModel()
        ret = block()
        updateView()
        inTransaction = false
      } catch(e: EViewModel) {
        updateView(e)
        throw e
      } finally {
        inTransaction = false
      }
    }
    return ret
  }

  @Throws(EViewModel::class)
  fun execString(block: () -> String): String {
    return execValue(block) ?: ""
  }

  @Throws(EViewModel::class)
  fun execInt(block: () -> Int): Int {
    return execValue(block) ?: 0
  }

  @Throws(EViewModel::class)
  fun <T> execList(block: () -> List<T>): List<T> {
    return execValue(block).orEmpty()
  }

  private fun <T> transaction(block: () -> T) {
    try {
      Transaction.execTransacao {block()}
    } catch(e: EViewModel) {
      //Não faz nada
    }
  }

  protected fun showWarning(msg: String) {
    view.showWarning(msg)
  }
}

open class EViewModel(msg: String): Exception(msg)

interface IView {
  fun updateView()
  fun updateModel()
  fun showWarning(msg: String)
  fun showError(msg: String)
  fun showInfo(msg: String)
}

