package br.com.engecopi.framework.viewmodel

import br.com.engecopi.framework.model.Transaction
import java.lang.reflect.Proxy

abstract class ViewModel<V: IView>(val view: V): IViewModel {
  private var inTransaction = false
  
  private fun showException(exception: Exception) {
    exception.message?.let {message ->
      when(exception) {
        is EViewModelError   -> view.showError(message)
        is EViewModelWarning -> view.showWarning(message)
        is EViewModelInfo    -> view.showInfo(message)
      }
    }
  }
  
  @Throws(EViewModelError::class)
  fun <T> exec(block: () -> T): T {
    return if(inTransaction) block()
    else transaction {
      try {
        inTransaction = true
        val ret = block()
        inTransaction = false
        ret
      } catch(e: EViewModelError) {
        showException(e)
        throw e
      } catch(e: EViewModelWarning) {
        showException(e)
        throw e
      } catch(e: EViewModelInfo) {
        showException(e)
        throw e
      } finally {
        inTransaction = false
      }
    }
  }
  
  private fun <T> transaction(block: () -> T): T {
    return Transaction.execTransacao {block()}
  }
  
  protected fun showWarning(msg: String) {
    view.showWarning(msg)
  }
}

open class EViewModelError(msg: String): Exception(msg)

open class EViewModelWarning(msg: String): Exception(msg)

open class EViewModelInfo(msg: String): Exception(msg)

interface IView {
  fun showWarning(msg: String)
  fun showError(msg: String)
  fun showInfo(msg: String)
}


