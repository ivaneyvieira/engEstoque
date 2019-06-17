package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewProdutoSaci
import br.com.engecopi.estoque.model.query.QViewProdutoSaci
import io.ebean.Finder

open class ViewProdutoSaciFinder : Finder<String, ViewProdutoSaci>(ViewProdutoSaci::class.java) {
  /**
   * Start a new typed query.
   */
  fun where(): QViewProdutoSaci {
    return QViewProdutoSaci(db())
  }
}