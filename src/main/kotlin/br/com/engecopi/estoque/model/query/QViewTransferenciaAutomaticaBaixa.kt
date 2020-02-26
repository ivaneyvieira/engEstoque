package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewTransferenciaAutomaticaBaixa
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewTransferenciaAutomaticaBaixa.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewTransferenciaAutomaticaBaixa : TQRootBean<ViewTransferenciaAutomaticaBaixa, QViewTransferenciaAutomaticaBaixa> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewTransferenciaAutomaticaBaixa(true)
  }

  lateinit var id: PString<QViewTransferenciaAutomaticaBaixa>
  lateinit var storeno: PInteger<QViewTransferenciaAutomaticaBaixa>
  lateinit var pdvno: PInteger<QViewTransferenciaAutomaticaBaixa>
  lateinit var xano: PInteger<QViewTransferenciaAutomaticaBaixa>
  lateinit var data: PInteger<QViewTransferenciaAutomaticaBaixa>
  lateinit var storenoFat: PInteger<QViewTransferenciaAutomaticaBaixa>
  lateinit var nffat: PString<QViewTransferenciaAutomaticaBaixa>
  lateinit var storenoTransf: PInteger<QViewTransferenciaAutomaticaBaixa>
  lateinit var nftransf: PString<QViewTransferenciaAutomaticaBaixa>
  lateinit var loja: QAssocLoja<QViewTransferenciaAutomaticaBaixa>
  lateinit var nota: QAssocNota<QViewTransferenciaAutomaticaBaixa>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewTransferenciaAutomaticaBaixa::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewTransferenciaAutomaticaBaixa::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
