package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.query.QLoja
import io.ebean.typequery.*

/**
 * Association query bean for AssocLoja.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocLoja<R>(name: String, root: R) : TQAssocBean<Loja, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var numero: PInteger<R>
  lateinit var sigla: PString<R>
  lateinit var notas: QAssocNota<R>
  lateinit var usuarios: QAssocUsuario<R>
  lateinit var viewProdutoLoc: QAssocViewProdutoLoc<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QLoja>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QLoja>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QLoja>): R {
    return fetchLazyProperties(*properties)
  }

}
