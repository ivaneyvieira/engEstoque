package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.query.assoc.QAssocItemNota
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import io.ebean.Database
import io.ebean.typequery.PBoolean
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLocalDateTime
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for Usuario.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QUsuario : TQRootBean<Usuario, QUsuario> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QUsuario(true)
  }

  lateinit var id: PLong<QUsuario>
  lateinit var createdAt: PLocalDateTime<QUsuario>
  lateinit var updatedAt: PLocalDateTime<QUsuario>
  lateinit var version: PInteger<QUsuario>
  lateinit var loginName: PString<QUsuario>
  lateinit var loja: QAssocLoja<QUsuario>
  lateinit var localizacaoes: PString<QUsuario>
  lateinit var notaSeries: PString<QUsuario>
  lateinit var itensNota: QAssocItemNota<QUsuario>
  lateinit var admin: PBoolean<QUsuario>
  lateinit var estoque: PBoolean<QUsuario>
  lateinit var expedicao: PBoolean<QUsuario>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(Usuario::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(Usuario::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
