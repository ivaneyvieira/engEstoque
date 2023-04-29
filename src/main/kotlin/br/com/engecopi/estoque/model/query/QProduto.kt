package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.query.assoc.QAssocItemNota
import br.com.engecopi.estoque.model.query.assoc.QAssocViewProduto
import br.com.engecopi.estoque.model.query.assoc.QAssocViewProdutoLoc
import io.ebean.Database
import io.ebean.typequery.*

/**
 * Query bean for Produto.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QProduto : TQRootBean<Produto, QProduto> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QProduto(true)
  }

  lateinit var id: PLong<QProduto>
  lateinit var createdAt: PLocalDateTime<QProduto>
  lateinit var updatedAt: PLocalDateTime<QProduto>
  lateinit var version: PInteger<QProduto>
  lateinit var codigo: PString<QProduto>
  lateinit var grade: PString<QProduto>
  lateinit var mesesVencimento: PInteger<QProduto>
  lateinit var quantidadePacote: PInteger<QProduto>
  lateinit var codebar: PString<QProduto>
  lateinit var dataCadastro: PLocalDate<QProduto>
  lateinit var itensNota: QAssocItemNota<QProduto>
  lateinit var vproduto: QAssocViewProduto<QProduto>
  lateinit var viewProdutoLoc: QAssocViewProdutoLoc<QProduto>
  lateinit var localizacao: PString<QProduto>
  lateinit var saldo_total: PInteger<QProduto>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(Produto::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(Produto::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
