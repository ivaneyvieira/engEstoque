package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewEntregaFuturaBaixa
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewEntregaFuturaBaixa.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewEntregaFuturaBaixa : TQRootBean<ViewEntregaFuturaBaixa, QViewEntregaFuturaBaixa> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewEntregaFuturaBaixa(true)
  }

  lateinit var id: PString<QViewEntregaFuturaBaixa>
  lateinit var storenoVenda: PInteger<QViewEntregaFuturaBaixa>
  lateinit var numeroVenda: PString<QViewEntregaFuturaBaixa>
  lateinit var nfnoVenda: PInteger<QViewEntregaFuturaBaixa>
  lateinit var nfseVenda: PString<QViewEntregaFuturaBaixa>
  lateinit var dataVenda: PInteger<QViewEntregaFuturaBaixa>
  lateinit var storenoEntrega: PInteger<QViewEntregaFuturaBaixa>
  lateinit var numeroEntrega: PString<QViewEntregaFuturaBaixa>
  lateinit var nfnoEntrega: PInteger<QViewEntregaFuturaBaixa>
  lateinit var nfseEntrega: PString<QViewEntregaFuturaBaixa>
  lateinit var dataEntrega: PInteger<QViewEntregaFuturaBaixa>
  lateinit var nfekeyEntrega: PString<QViewEntregaFuturaBaixa>
  lateinit var loja: QAssocLoja<QViewEntregaFuturaBaixa>
  lateinit var nota: QAssocNota<QViewEntregaFuturaBaixa>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewEntregaFuturaBaixa::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewEntregaFuturaBaixa::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
