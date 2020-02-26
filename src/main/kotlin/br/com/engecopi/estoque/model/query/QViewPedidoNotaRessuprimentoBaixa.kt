package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewPedidoNotaRessuprimentoBaixa
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewPedidoNotaRessuprimentoBaixa.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewPedidoNotaRessuprimentoBaixa : TQRootBean<ViewPedidoNotaRessuprimentoBaixa, QViewPedidoNotaRessuprimentoBaixa> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewPedidoNotaRessuprimentoBaixa(true)
  }

  lateinit var id: PString<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var storenoPedido: PInteger<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var ordno: PInteger<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var dataPedido: PInteger<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var storenoNota: PInteger<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var nfno: PInteger<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var nfse: PString<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var numero: PString<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var dataNota: PInteger<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var loja: QAssocLoja<QViewPedidoNotaRessuprimentoBaixa>
  lateinit var nota: QAssocNota<QViewPedidoNotaRessuprimentoBaixa>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewPedidoNotaRessuprimentoBaixa::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewPedidoNotaRessuprimentoBaixa::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
