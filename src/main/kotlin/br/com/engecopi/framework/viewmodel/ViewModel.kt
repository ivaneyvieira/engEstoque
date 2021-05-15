package br.com.engecopi.framework.viewmodel

import br.com.engecopi.framework.model.EModel
import br.com.engecopi.framework.model.Transaction

abstract class ViewModel<V : IView>(val view: V) : IViewModel {
  private var inTransaction = false

  private fun showException(exception: Exception) {
    view.showError(exception.message ?: "Erro desconhecido")
  }

  @Throws(EViewModelError::class) fun <T> execList(block: () -> List<T>): List<T> {
    return exec(block) ?: emptyList()
  }

  @Throws(EViewModelError::class) fun execUnit(block: () -> Unit) {
    exec(block)
  }

  @Throws(EViewModelError::class) fun execString(block: () -> String): String {
    return exec(block) ?: ""
  }

  @Throws(EViewModelError::class) fun execInt(block: () -> Int): Int {
    return exec(block) ?: 0
  }

  @Throws(EViewModelError::class) fun <T> exec(block: () -> T): T? {
    return if (inTransaction) block()
    else transaction {
      try {
        inTransaction = true
        val ret = block()
        inTransaction = false
        ret
      } catch (e: EViewModelError) {
        showException(e)
        throw e
      } catch (e: EModel) {
        showException(e)
        throw e
      } catch (e: Throwable) {
        throw e
      } finally {
        inTransaction = false
      }
    }
  }

  private fun <T> transaction(block: () -> T): T? {
    return try {
      Transaction.execTransacao { block() }
    } catch (e: Throwable) {
      null
    }
  }

  protected fun showWarning(msg: String) {
    view.showWarning(msg)
  }
}

interface IView {
  fun showWarning(msg: String)
  fun showError(msg: String)
  fun showInfo(msg: String)
}


