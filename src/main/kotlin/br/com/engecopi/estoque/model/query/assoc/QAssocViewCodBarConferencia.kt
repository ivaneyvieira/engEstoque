package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.estoque.model.query.QViewCodBarConferencia
import io.ebean.typequery.*

/**
 * Association query bean for AssocViewCodBarConferencia.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewCodBarConferencia<R>(name: String, root: R) : TQAssocBean<ViewCodBarConferencia, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var codbar: PString<R>
  lateinit var storeno: PInteger<R>
  lateinit var numero: PString<R>
  lateinit var sequencia: PInteger<R>
  lateinit var abreviacao: PString<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewCodBarConferencia>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewCodBarConferencia>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewCodBarConferencia>): R {
    return fetchLazyProperties(*properties)
  }

}
