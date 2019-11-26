package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.HistoricoEtiqueta
import br.com.engecopi.estoque.model.query.QHistoricoEtiqueta
import io.ebean.Finder

open class HistoricoEtiquetaFinder: Finder<Long, HistoricoEtiqueta>(HistoricoEtiqueta::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QHistoricoEtiqueta {
    return QHistoricoEtiqueta(db())
  }
}
