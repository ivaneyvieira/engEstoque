package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewCodBarCliente
import br.com.engecopi.estoque.model.query.QViewCodBarCliente
import io.ebean.typequery.*

/**
 * Association query bean for AssocViewCodBarCliente.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean class QAssocViewCodBarCliente<R>(name: String, root: R) : TQAssocBean<ViewCodBarCliente, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var codbar: PString<R>
  lateinit var codbarLimpo: PString<R>
  lateinit var codbarNota: PString<R>
  lateinit var storeno: PInteger<R>
  lateinit var numero: PString<R>
  lateinit var sequencia: PInteger<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewCodBarCliente>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewCodBarCliente>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewCodBarCliente>): R {
    return fetchLazyProperties(*properties)
  }

}
