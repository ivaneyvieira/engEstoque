package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewCodBarEntrega
import br.com.engecopi.estoque.model.query.QViewCodBarEntrega
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQAssocBean
import io.ebean.typequery.TQProperty
import io.ebean.typequery.TypeQueryBean

/**
 * Association query bean for AssocViewCodBarEntrega.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewCodBarEntrega<R>(name: String, root: R): TQAssocBean<ViewCodBarEntrega, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var codbar: PString<R>
  lateinit var storeno: PInteger<R>
  lateinit var numero: PString<R>
  lateinit var sequencia: PInteger<R>
  lateinit var abreviacao: PString<R>
  lateinit var codigo: PString<R>
  lateinit var grade: PString<R>
  lateinit var quantidade: PInteger<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewCodBarEntrega>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewCodBarEntrega>): R {
    return fetchQueryProperties(*properties)
  }
  
  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewCodBarEntrega>): R {
    return fetchLazyProperties(*properties)
  }
}
