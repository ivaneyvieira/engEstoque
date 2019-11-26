package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.HistoricoEtiqueta
import br.com.engecopi.estoque.model.query.QHistoricoEtiqueta
import io.ebean.typequery.PBoolean
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLocalDate
import io.ebean.typequery.PLocalDateTime
import io.ebean.typequery.PLocalTime
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQAssocBean
import io.ebean.typequery.TQProperty
import io.ebean.typequery.TypeQueryBean

/**
 * Association query bean for AssocHistoricoEtiqueta.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocHistoricoEtiqueta<R>(name: String, root: R): TQAssocBean<HistoricoEtiqueta, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var usuario: QAssocUsuario<R>
  lateinit var produto: QAssocProduto<R>
  lateinit var data: PLocalDate<R>
  lateinit var hora: PLocalTime<R>
  lateinit var print: PString<R>
  lateinit var gtin: PString<R>
  lateinit var gtinOk: PBoolean<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QHistoricoEtiqueta>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QHistoricoEtiqueta>): R {
    return fetchQueryProperties(*properties)
  }
  
  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QHistoricoEtiqueta>): R {
    return fetchLazyProperties(*properties)
  }
}
