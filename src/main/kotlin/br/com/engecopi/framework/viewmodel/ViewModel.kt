package br.com.engecopi.framework.viewmodel

import br.com.engecopi.framework.model.Transaction

abstract class ViewModel<V: IView>(val view: V): IViewModel {
  private var inTransaction = false
  
  private fun updateView(exception: EViewModel) {
    exception.message?.let {message ->
      view.showError(message)
    }
  }
  
  @Throws(EViewModel::class)
  fun exec(block: () -> Unit) {
    try {
      if(inTransaction) block()
      else transaction {
        inTransaction = true
        block()
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
        ret = block()
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
  fun showWarning(msg: String)
  fun showError(msg: String)
  fun showInfo(msg: String)
}

