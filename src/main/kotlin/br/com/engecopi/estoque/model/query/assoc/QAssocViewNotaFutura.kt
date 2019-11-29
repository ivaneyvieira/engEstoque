package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewNotaFutura
import br.com.engecopi.estoque.model.query.QViewNotaFutura
import io.ebean.typequery.PEnum
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
 * Association query bean for AssocViewNotaFutura.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewNotaFutura<R>(name: String, root: R): TQAssocBean<ViewNotaFutura, R>(name, root) {
  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var nota: QAssocNota<R>
  lateinit var numero: PString<R>
  lateinit var tipoMov: PEnum<R, TipoMov>
  lateinit var tipoNota: PEnum<R, TipoNota>
  lateinit var rota: PString<R>
  lateinit var fornecedor: PString<R>
  lateinit var cliente: PString<R>
  lateinit var data: PLocalDate<R>
  lateinit var dataEmissao: PLocalDate<R>
  lateinit var lancamento: PLocalDate<R>
  lateinit var hora: PLocalTime<R>
  lateinit var observacao: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var sequencia: PInteger<R>
  lateinit var usuario: QAssocUsuario<R>
  lateinit var abreviacao: PString<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewNotaFutura>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewNotaFutura>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewNotaFutura>): R {
    return fetchLazyProperties(*properties)
  }
}
