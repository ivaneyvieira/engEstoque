package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewCodBarEntrega
import br.com.engecopi.estoque.model.query.QViewCodBarEntrega
import io.ebean.Finder

open class ViewCodBarEntregaFinder: Finder<Long, ViewCodBarEntrega>(ViewCodBarEntrega::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QViewCodBarEntrega {
    return QViewCodBarEntrega(db())
  }
}