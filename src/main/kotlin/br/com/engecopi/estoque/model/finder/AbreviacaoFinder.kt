package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.query.QAbreviacao
import io.ebean.Finder

open class AbreviacaoFinder : Finder<Long, Abreviacao>(Abreviacao::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QAbreviacao {
    return QAbreviacao(db())
  }
}