package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewEntregaFuturaBaixa
import br.com.engecopi.estoque.model.query.QViewEntregaFuturaBaixa
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQAssocBean
import io.ebean.typequery.TQProperty
import io.ebean.typequery.TypeQueryBean

/**
 * Association query bean for AssocViewEntregaFuturaBaixa.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewEntregaFuturaBaixa<R>(name: String, root: R) : TQAssocBean<ViewEntregaFuturaBaixa,R>(name, root) {

  lateinit var id: PString<R>
  lateinit var storenoVenda: PInteger<R>
  lateinit var numeroVenda: PString<R>
  lateinit var nfnoVenda: PInteger<R>
  lateinit var nfseVenda: PString<R>
  lateinit var dataVenda: PInteger<R>
  lateinit var storenoEntrega: PInteger<R>
  lateinit var numeroEntrega: PString<R>
  lateinit var nfnoEntrega: PInteger<R>
  lateinit var nfseEntrega: PString<R>
  lateinit var dataEntrega: PInteger<R>
  lateinit var nfekeyEntrega: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var nota: QAssocNota<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewEntregaFuturaBaixa>) : R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewEntregaFuturaBaixa>) : R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewEntregaFuturaBaixa>) : R {
    return fetchLazyProperties(*properties)
  }

}
