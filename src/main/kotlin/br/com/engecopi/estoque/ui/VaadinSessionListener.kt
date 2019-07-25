package br.com.engecopi.estoque.ui

import com.vaadin.server.*

object VaadinSessionListener {
  val setSessions = mutableSetOf<VaadinSession>()

  class VaadinSessionInitListener: SessionInitListener {
    @Throws(ServiceException::class)
    override fun sessionInit(event: SessionInitEvent) {
      event.session?.let {setSessions.add(it)}
    }
  }

  class VaadinSessionDestroyListener: SessionDestroyListener {
    override fun sessionDestroy(event: SessionDestroyEvent) {
      if(event.session != null && event.session.session != null) {
        event.session?.let {setSessions.remove(it)}
      }
    }
  }

  val sessions
    get() = setSessions.toList()
  val uis
    get() = sessions.flatMap {it.uIs}.filterNotNull().distinct()
}