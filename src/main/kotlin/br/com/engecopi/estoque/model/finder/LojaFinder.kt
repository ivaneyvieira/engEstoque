package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.query.QLoja
import io.ebean.Finder

open class LojaFinder: Finder<Long, Loja>(Loja::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QLoja {
    return QLoja(db())
  }
}