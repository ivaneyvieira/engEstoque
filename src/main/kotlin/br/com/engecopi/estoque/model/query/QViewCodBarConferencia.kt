package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewCodBarConferencia
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewCodBarConferencia.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewCodBarConferencia : TQRootBean<ViewCodBarConferencia, QViewCodBarConferencia> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewCodBarConferencia(true)
  }

  lateinit var id: PLong<QViewCodBarConferencia>
  lateinit var codbar: PString<QViewCodBarConferencia>
  lateinit var storeno: PInteger<QViewCodBarConferencia>
  lateinit var numero: PString<QViewCodBarConferencia>
  lateinit var sequencia: PInteger<QViewCodBarConferencia>
  lateinit var abreviacao: PString<QViewCodBarConferencia>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewCodBarConferencia::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewCodBarConferencia::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
