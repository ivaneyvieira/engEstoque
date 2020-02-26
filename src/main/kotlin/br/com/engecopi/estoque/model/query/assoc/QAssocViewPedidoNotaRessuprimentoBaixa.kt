package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.ViewPedidoNotaRessuprimentoBaixa
import br.com.engecopi.estoque.model.query.QViewPedidoNotaRessuprimentoBaixa
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQAssocBean
import io.ebean.typequery.TQProperty
import io.ebean.typequery.TypeQueryBean

/**
 * Association query bean for AssocViewPedidoNotaRessuprimentoBaixa.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocViewPedidoNotaRessuprimentoBaixa<R>(name: String, root: R) : TQAssocBean<ViewPedidoNotaRessuprimentoBaixa,R>(name, root) {

  lateinit var id: PString<R>
  lateinit var storenoPedido: PInteger<R>
  lateinit var ordno: PInteger<R>
  lateinit var dataPedido: PInteger<R>
  lateinit var storenoNota: PInteger<R>
  lateinit var nfno: PInteger<R>
  lateinit var nfse: PString<R>
  lateinit var numero: PString<R>
  lateinit var dataNota: PInteger<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var nota: QAssocNota<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewPedidoNotaRessuprimentoBaixa>) : R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewPedidoNotaRessuprimentoBaixa>) : R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewPedidoNotaRessuprimentoBaixa>) : R {
    return fetchLazyProperties(*properties)
  }

}
