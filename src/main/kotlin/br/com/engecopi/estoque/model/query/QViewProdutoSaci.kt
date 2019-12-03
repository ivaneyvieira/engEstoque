package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ViewProdutoSaci
import io.ebean.Database
import io.ebean.typequery.PDouble
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewProdutoSaci.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewProdutoSaci: TQRootBean<ViewProdutoSaci, QViewProdutoSaci> {
  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewProdutoSaci(true)
  }
  
  lateinit var id: PString<QViewProdutoSaci>
  lateinit var codigo: PString<QViewProdutoSaci>
  lateinit var nome: PString<QViewProdutoSaci>
  lateinit var grade: PString<QViewProdutoSaci>
  lateinit var codebar: PString<QViewProdutoSaci>
  lateinit var custo: PDouble<QViewProdutoSaci>
  lateinit var unidade: PString<QViewProdutoSaci>
  lateinit var tipo: PString<QViewProdutoSaci>
  
  /**
   * Construct with a given Database.
   */
  constructor(database: Database): super(ViewProdutoSaci::class.java, database)
  
  /**
   * Construct using the default Database.
   */
  constructor(): super(ViewProdutoSaci::class.java)
  
  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean): super(dummy)
}
