package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.query.QAbreviacao
import io.ebean.typequery.*

/**
 * Association query bean for AssocAbreviacao.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocAbreviacao<R>(name: String, root: R) : TQAssocBean<Abreviacao, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var abreviacao: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var expedicao: PBoolean<R>
  lateinit var impressora: PString<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QAbreviacao>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QAbreviacao>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QAbreviacao>): R {
    return fetchLazyProperties(*properties)
  }
}
