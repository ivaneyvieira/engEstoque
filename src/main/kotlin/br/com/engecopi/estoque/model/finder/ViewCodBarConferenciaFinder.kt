package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.estoque.model.query.QViewCodBarConferencia
import io.ebean.Finder

open class ViewCodBarConferenciaFinder : Finder<Long, ViewCodBarConferencia>(ViewCodBarConferencia::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QViewCodBarConferencia {
    return QViewCodBarConferencia(db())
  }
}