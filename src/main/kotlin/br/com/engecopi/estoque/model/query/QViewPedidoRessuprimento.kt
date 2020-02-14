package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewPedidoRessuprimento
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import io.ebean.Database
import io.ebean.typequery.PEnum
import io.ebean.typequery.PInteger
import io.ebean.typequery.PLocalDate
import io.ebean.typequery.PLocalDateTime
import io.ebean.typequery.PLocalTime
import io.ebean.typequery.PLong
import io.ebean.typequery.PString
import io.ebean.typequery.TQRootBean
import io.ebean.typequery.TypeQueryBean

/**
 * Query bean for ViewPedidoRessuprimento.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewPedidoRessuprimento : TQRootBean<ViewPedidoRessuprimento, QViewPedidoRessuprimento> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewPedidoRessuprimento(true)
  }

  lateinit var id: PLong<QViewPedidoRessuprimento>
  lateinit var createdAt: PLocalDateTime<QViewPedidoRessuprimento>
  lateinit var updatedAt: PLocalDateTime<QViewPedidoRessuprimento>
  lateinit var version: PInteger<QViewPedidoRessuprimento>
  lateinit var nota: QAssocNota<QViewPedidoRessuprimento>
  lateinit var numero: PString<QViewPedidoRessuprimento>
  lateinit var tipoMov: PEnum<QViewPedidoRessuprimento,TipoMov>
  lateinit var tipoNota: PEnum<QViewPedidoRessuprimento,TipoNota>
  lateinit var rota: PString<QViewPedidoRessuprimento>
  lateinit var fornecedor: PString<QViewPedidoRessuprimento>
  lateinit var cliente: PString<QViewPedidoRessuprimento>
  lateinit var data: PLocalDate<QViewPedidoRessuprimento>
  lateinit var dataEmissao: PLocalDate<QViewPedidoRessuprimento>
  lateinit var lancamento: PLocalDate<QViewPedidoRessuprimento>
  lateinit var hora: PLocalTime<QViewPedidoRessuprimento>
  lateinit var observacao: PString<QViewPedidoRessuprimento>
  lateinit var loja: QAssocLoja<QViewPedidoRessuprimento>
  lateinit var sequencia: PInteger<QViewPedidoRessuprimento>
  lateinit var usuario: QAssocUsuario<QViewPedidoRessuprimento>
  lateinit var abreviacao: PString<QViewPedidoRessuprimento>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewPedidoRessuprimento::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewPedidoRessuprimento::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
