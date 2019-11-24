package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewCodBarCliente
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewCodBarCliente.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewCodBarCliente: TQRootBean<ViewCodBarCliente, QViewCodBarCliente> {
  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewCodBarCliente(true)
  }
  
  lateinit var id: PLong<QViewCodBarCliente>
  lateinit var codbar: PString<QViewCodBarCliente>
  lateinit var codbarLimpo: PString<QViewCodBarCliente>
  lateinit var codbarNota: PString<QViewCodBarCliente>
  lateinit var storeno: PInteger<QViewCodBarCliente>
  lateinit var numero: PString<QViewCodBarCliente>
  lateinit var sequencia: PInteger<QViewCodBarCliente>
  
  /**
   * Construct with a given Database.
   */
  constructor(database: Database): super(ViewCodBarCliente::class.java, database)
  
  /**
   * Construct using the default Database.
   */
  constructor(): super(ViewCodBarCliente::class.java)
  
  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean): super(dummy)
}
