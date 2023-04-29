package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.Validade
import io.ebean.Database
import io.ebean.typequery.*

/**
 * Query bean for Validade.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QValidade : TQRootBean<Validade, QValidade> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QValidade(true)
  }

  lateinit var id: PLong<QValidade>
  lateinit var createdAt: PLocalDateTime<QValidade>
  lateinit var updatedAt: PLocalDateTime<QValidade>
  lateinit var version: PInteger<QValidade>
  lateinit var mesesValidade: PInteger<QValidade>
  lateinit var mesesFabricacao: PInteger<QValidade>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(Validade::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(Validade::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
