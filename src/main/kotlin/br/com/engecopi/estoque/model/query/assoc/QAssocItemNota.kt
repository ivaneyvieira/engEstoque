package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.query.QItemNota
import io.ebean.typequery.PBoolean
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
 * Association query bean for AssocItemNota.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocItemNota<R>(name: String, root: R): TQAssocBean<ItemNota, R>(name, root) {
  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var data: PLocalDate<R>
  lateinit var hora: PLocalTime<R>
  lateinit var quantidade: PInteger<R>
  lateinit var quantidadeSaci: PInteger<R>
  lateinit var produto: QAssocProduto<R>
  lateinit var nota: QAssocNota<R>
  lateinit var etiqueta: QAssocEtiqueta<R>
  lateinit var usuario: QAssocUsuario<R>
  lateinit var saldo: PInteger<R>
  lateinit var impresso: PBoolean<R>
  lateinit var localizacao: PString<R>
  lateinit var status: PEnum<R, StatusNota>
  lateinit var codigoBarraCliente: PString<R>
  lateinit var codigoBarraConferencia: PString<R>
  lateinit var codigoBarraConferenciaBaixa: PString<R>
  lateinit var codigoBarraEntrega: PString<R>
  
  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QItemNota>): R {
    return fetchProperties(*properties)
  }
  
  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QItemNota>): R {
    return fetchQueryProperties(*properties)
  }
  
  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QItemNota>): R {
    return fetchLazyProperties(*properties)
  }
}
