package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewPedidoAbastecimento
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import io.ebean.Database
import io.ebean.typequery.*

/**
 * Query bean for ViewPedidoAbastecimento.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewPedidoAbastecimento : TQRootBean<ViewPedidoAbastecimento, QViewPedidoAbastecimento> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewPedidoAbastecimento(true)
  }

  lateinit var id: PLong<QViewPedidoAbastecimento>
  lateinit var createdAt: PLocalDateTime<QViewPedidoAbastecimento>
  lateinit var updatedAt: PLocalDateTime<QViewPedidoAbastecimento>
  lateinit var version: PInteger<QViewPedidoAbastecimento>
  lateinit var nota: QAssocNota<QViewPedidoAbastecimento>
  lateinit var numero: PString<QViewPedidoAbastecimento>
  lateinit var tipoMov: PEnum<QViewPedidoAbastecimento, TipoMov>
  lateinit var tipoNota: PEnum<QViewPedidoAbastecimento, TipoNota>
  lateinit var rota: PString<QViewPedidoAbastecimento>
  lateinit var fornecedor: PString<QViewPedidoAbastecimento>
  lateinit var cliente: PString<QViewPedidoAbastecimento>
  lateinit var data: PLocalDate<QViewPedidoAbastecimento>
  lateinit var dataEmissao: PLocalDate<QViewPedidoAbastecimento>
  lateinit var lancamento: PLocalDate<QViewPedidoAbastecimento>
  lateinit var hora: PLocalTime<QViewPedidoAbastecimento>
  lateinit var observacao: PString<QViewPedidoAbastecimento>
  lateinit var loja: QAssocLoja<QViewPedidoAbastecimento>
  lateinit var sequencia: PInteger<QViewPedidoAbastecimento>
  lateinit var usuario: QAssocUsuario<QViewPedidoAbastecimento>
  lateinit var abreviacao: PString<QViewPedidoAbastecimento>
  lateinit var codigoBarraConferencia: PString<QViewPedidoAbastecimento>

  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewPedidoAbastecimento::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewPedidoAbastecimento::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
