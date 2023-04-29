package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import io.ebean.Database
import io.ebean.typequery.*

/**
 * Query bean for Abreviacao.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAbreviacao : TQRootBean<Abreviacao, QAbreviacao> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QAbreviacao(true)
  }

  lateinit var id: PLong<QAbreviacao>
  lateinit var createdAt: PLocalDateTime<QAbreviacao>
  lateinit var updatedAt: PLocalDateTime<QAbreviacao>
  lateinit var version: PInteger<QAbreviacao>
  lateinit var abreviacao: PString<QAbreviacao>
  lateinit var loja: QAssocLoja<QAbreviacao>
  lateinit var expedicao: PBoolean<QAbreviacao>
  lateinit var impressora: PString<QAbreviacao>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(Abreviacao::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(Abreviacao::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
