package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.query.QViewProdutoLoc
import io.ebean.Finder

open class ViewProdutoLocFinder : Finder<String, ViewProdutoLoc>(ViewProdutoLoc::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QViewProdutoLoc {
    return QViewProdutoLoc(db())
  }
}