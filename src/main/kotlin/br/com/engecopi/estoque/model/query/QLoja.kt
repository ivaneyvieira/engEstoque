package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import br.com.engecopi.estoque.model.query.assoc.QAssocViewProdutoLoc
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLocalDateTime
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for Loja.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QLoja: TQRootBean<Loja, QLoja> {
  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QLoja(true)
  }
  
  lateinit var id: PLong<QLoja>
  lateinit var createdAt: PLocalDateTime<QLoja>
  lateinit var updatedAt: PLocalDateTime<QLoja>
  lateinit var version: PInteger<QLoja>
  lateinit var numero: PInteger<QLoja>
  lateinit var sigla: PString<QLoja>
  lateinit var notas: QAssocNota<QLoja>
  lateinit var usuarios: QAssocUsuario<QLoja>
  lateinit var viewProdutoLoc: QAssocViewProdutoLoc<QLoja>
  
  /**
   * Construct with a given Database.
   */
  constructor(database: Database): super(Loja::class.java, database)
  
  /**
   * Construct using the default Database.
   */
  constructor(): super(Loja::class.java)
  
  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean): super(dummy)
}
