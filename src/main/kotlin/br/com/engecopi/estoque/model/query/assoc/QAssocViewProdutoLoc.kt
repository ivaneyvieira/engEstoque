package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.query.QViewProdutoLoc
import io.ebean.typequery.*

/**
 * Association query bean for AssocViewProdutoLoc.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewProdutoLoc<R>(name: String, root: R) : TQAssocBean<ViewProdutoLoc, R>(name, root) {

  lateinit var id: PString<R>
  lateinit var storeno: PInteger<R>
  lateinit var codigo: PString<R>
  lateinit var grade: PString<R>
  lateinit var localizacao: PString<R>
  lateinit var abreviacao: PString<R>
  lateinit var produto: QAssocProduto<R>
  lateinit var loja: QAssocLoja<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewProdutoLoc>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewProdutoLoc>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewProdutoLoc>): R {
    return fetchLazyProperties(*properties)
  }
}
