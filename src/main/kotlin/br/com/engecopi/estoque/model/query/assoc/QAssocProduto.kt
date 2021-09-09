package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.query.QProduto
import io.ebean.typequery.*

/**
 * Association query bean for AssocProduto.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocProduto<R>(name: String, root: R) : TQAssocBean<Produto, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var codigo: PString<R>
  lateinit var grade: PString<R>
  lateinit var codebar: PString<R>
  lateinit var dataCadastro: PLocalDate<R>
  lateinit var itensNota: QAssocItemNota<R>
  lateinit var vproduto: QAssocViewProduto<R>
  lateinit var viewProdutoLoc: QAssocViewProdutoLoc<R>
  lateinit var localizacao: PString<R>
  lateinit var saldo_total: PInteger<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QProduto>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QProduto>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QProduto>): R {
    return fetchLazyProperties(*properties)
  }
}
