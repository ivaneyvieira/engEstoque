package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewTransferenciaAutomaticaBaixa
import br.com.engecopi.estoque.model.query.QViewTransferenciaAutomaticaBaixa
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQAssocBean
import io.ebean.typequery.TQProperty
import io.ebean.typequery.TypeQueryBean

/**
 * Association query bean for AssocViewTransferenciaAutomaticaBaixa.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewTransferenciaAutomaticaBaixa<R>(name: String, root: R) : TQAssocBean<ViewTransferenciaAutomaticaBaixa,R>(name, root) {

  lateinit var id: PString<R>
  lateinit var storeno: PInteger<R>
  lateinit var pdvno: PInteger<R>
  lateinit var xano: PInteger<R>
  lateinit var data: PInteger<R>
  lateinit var storenoFat: PInteger<R>
  lateinit var nffat: PString<R>
  lateinit var storenoTransf: PInteger<R>
  lateinit var nftransf: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var nota: QAssocNota<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewTransferenciaAutomaticaBaixa>) : R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewTransferenciaAutomaticaBaixa>) : R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewTransferenciaAutomaticaBaixa>) : R {
    return fetchLazyProperties(*properties)
  }

}
