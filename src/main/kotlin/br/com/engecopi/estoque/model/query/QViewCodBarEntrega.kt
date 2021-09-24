package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewCodBarEntrega
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewCodBarEntrega.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewCodBarEntrega : TQRootBean<ViewCodBarEntrega, QViewCodBarEntrega> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewCodBarEntrega(true)
  }

  lateinit var id: PLong<QViewCodBarEntrega>
  lateinit var codbar: PString<QViewCodBarEntrega>
  lateinit var storeno: PInteger<QViewCodBarEntrega>
  lateinit var numero: PString<QViewCodBarEntrega>
  lateinit var sequencia: PInteger<QViewCodBarEntrega>
  lateinit var abreviacao: PString<QViewCodBarEntrega>
  lateinit var codigo: PString<QViewCodBarEntrega>
  lateinit var grade: PString<QViewCodBarEntrega>
  lateinit var quantidade: PInteger<QViewCodBarEntrega>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewCodBarEntrega::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewCodBarEntrega::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
