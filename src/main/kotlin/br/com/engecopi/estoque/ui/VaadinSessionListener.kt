package br.com.engecopi.estoque.ui

import com.vaadin.server.*

object VaadinSessionListener {
  @Volatile
  private var activeSessions = 0

  class VaadinSessionInitListener: SessionInitListener {
    @Throws(ServiceException::class)
    override fun sessionInit(event: SessionInitEvent) {
      incSessionCounter()
      println("############## COUNT = $activeSessions")
    }
  }

  class VaadinSessionDestroyListener: SessionDestroyListener {
    override fun sessionDestroy(event: SessionDestroyEvent) {
        if(event.session != null && event.session.session != null) {
        decSessionCounter()
        println("############## COUNT = $activeSessions")
      }
    }
  }

  fun getActiveSessions(): Int? {
    return activeSessions
  }

  @Synchronized
  private fun decSessionCounter() {
    if(activeSessions > 0) {
      activeSessions--
    }
  }

  @Synchronized
  private fun incSessionCounter() {
    activeSessions++
  }
}