package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.HistoricoEtiqueta
import br.com.engecopi.estoque.model.query.assoc.QAssocProduto
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import io.ebean.Database
import io.ebean.typequery.PBoolean
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLocalDate
import io.ebean.typequery.PLocalDateTime
import io.ebean.typequery.PLocalTime
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for HistoricoEtiqueta.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QHistoricoEtiqueta: TQRootBean<HistoricoEtiqueta, QHistoricoEtiqueta> {
  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QHistoricoEtiqueta(true)
  }
  
  lateinit var id: PLong<QHistoricoEtiqueta>
  lateinit var createdAt: PLocalDateTime<QHistoricoEtiqueta>
  lateinit var updatedAt: PLocalDateTime<QHistoricoEtiqueta>
  lateinit var version: PInteger<QHistoricoEtiqueta>
  lateinit var usuario: QAssocUsuario<QHistoricoEtiqueta>
  lateinit var produto: QAssocProduto<QHistoricoEtiqueta>
  lateinit var data: PLocalDate<QHistoricoEtiqueta>
  lateinit var hora: PLocalTime<QHistoricoEtiqueta>
  lateinit var print: PString<QHistoricoEtiqueta>
  lateinit var gtin: PString<QHistoricoEtiqueta>
  lateinit var gtinOk: PBoolean<QHistoricoEtiqueta>
  
  /**
   * Construct with a given Database.
   */
  constructor(database: Database): super(HistoricoEtiqueta::class.java, database)
  
  /**
   * Construct using the default Database.
   */
  constructor(): super(HistoricoEtiqueta::class.java)
  
  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean): super(dummy)
}
