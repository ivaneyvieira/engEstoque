package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewProdutoSaci
import br.com.engecopi.estoque.model.query.QViewProdutoSaci
import io.ebean.typequery.PDouble
import io.ebean.typequery.PString
import io.ebean.typequery.TQAssocBean
import io.ebean.typequery.TQProperty
import io.ebean.typequery.TypeQueryBean

/**
 * Association query bean for AssocViewProdutoSaci.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewProdutoSaci<R>(name: String, root: R): TQAssocBean<ViewProdutoSaci, R>(name, root) {

  lateinit var id: PString<R>
  lateinit var codigo: PString<R>
  lateinit var nome: PString<R>
  lateinit var grade: PString<R>
  lateinit var codebar: PString<R>
  lateinit var custo: PDouble<R>
  lateinit var unidade: PString<R>
  lateinit var tipo: PString<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewProdutoSaci>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewProdutoSaci>): R {
    return fetchQueryProperties(*properties)
  }
  
  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewProdutoSaci>): R {
    return fetchLazyProperties(*properties)
  }
}
