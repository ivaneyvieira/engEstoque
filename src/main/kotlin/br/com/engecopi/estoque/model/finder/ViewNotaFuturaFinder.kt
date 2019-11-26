package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewNotaFutura
import br.com.engecopi.estoque.model.query.QViewNotaFutura
import io.ebean.Finder

open class ViewNotaFuturaFinder: Finder<Long, ViewNotaFutura>(ViewNotaFutura::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QViewNotaFutura {
    return QViewNotaFutura(db())
  }
}
