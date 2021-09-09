package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocProduto
import io.ebean.Database
import io.ebean.typequery.PInteger
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewProdutoLoc.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewProdutoLoc : TQRootBean<ViewProdutoLoc, QViewProdutoLoc> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewProdutoLoc(true)
  }

  lateinit var id: PString<QViewProdutoLoc>
  lateinit var storeno: PInteger<QViewProdutoLoc>
  lateinit var codigo: PString<QViewProdutoLoc>
  lateinit var grade: PString<QViewProdutoLoc>
  lateinit var localizacao: PString<QViewProdutoLoc>
  lateinit var abreviacao: PString<QViewProdutoLoc>
  lateinit var produto: QAssocProduto<QViewProdutoLoc>
  lateinit var loja: QAssocLoja<QViewProdutoLoc>

  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewProdutoLoc::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewProdutoLoc::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
