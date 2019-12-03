package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.query.assoc.QAssocItemNota
import io.ebean.Database
import io.ebean.typequery.PBoolean
import io.ebean.typequery.PEnum
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLocalDateTime
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for Etiqueta.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QEtiqueta: TQRootBean<Etiqueta, QEtiqueta> {
  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QEtiqueta(true)
  }
  
  lateinit var id: PLong<QEtiqueta>
  lateinit var createdAt: PLocalDateTime<QEtiqueta>
  lateinit var updatedAt: PLocalDateTime<QEtiqueta>
  lateinit var version: PInteger<QEtiqueta>
  lateinit var titulo: PString<QEtiqueta>
  lateinit var statusNota: PEnum<QEtiqueta, StatusNota>
  lateinit var template: PString<QEtiqueta>
  lateinit var itensNota: QAssocItemNota<QEtiqueta>
  lateinit var etiquetaDefault: PBoolean<QEtiqueta>
  
  /**
   * Construct with a given Database.
   */
  constructor(database: Database): super(Etiqueta::class.java, database)
  
  /**
   * Construct using the default Database.
   */
  constructor(): super(Etiqueta::class.java)
  
  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean): super(dummy)
}
