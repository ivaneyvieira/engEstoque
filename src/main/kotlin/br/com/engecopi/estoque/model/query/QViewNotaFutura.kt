package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewNotaFutura
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
 * Query bean for ViewNotaFutura.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewNotaFutura: TQRootBean<ViewNotaFutura, QViewNotaFutura> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewNotaFutura(true)
  }

  lateinit var id: PLong<QViewNotaFutura>
  lateinit var createdAt: PLocalDateTime<QViewNotaFutura>
  lateinit var updatedAt: PLocalDateTime<QViewNotaFutura>
  lateinit var version: PInteger<QViewNotaFutura>
  lateinit var nota: QAssocNota<QViewNotaFutura>
  lateinit var numero: PString<QViewNotaFutura>
  lateinit var tipoMov: PEnum<QViewNotaFutura, TipoMov>
  lateinit var tipoNota: PEnum<QViewNotaFutura, TipoNota>
  lateinit var rota: PString<QViewNotaFutura>
  lateinit var fornecedor: PString<QViewNotaFutura>
  lateinit var cliente: PString<QViewNotaFutura>
  lateinit var data: PLocalDate<QViewNotaFutura>
  lateinit var dataEmissao: PLocalDate<QViewNotaFutura>
  lateinit var lancamento: PLocalDate<QViewNotaFutura>
  lateinit var hora: PLocalTime<QViewNotaFutura>
  lateinit var observacao: PString<QViewNotaFutura>
  lateinit var loja: QAssocLoja<QViewNotaFutura>
  lateinit var sequencia: PInteger<QViewNotaFutura>
  lateinit var usuario: QAssocUsuario<QViewNotaFutura>
  lateinit var abreviacao: PString<QViewNotaFutura>
  
  /**
   * Construct with a given Database.
   */
  constructor(database: Database): super(ViewNotaFutura::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor(): super(ViewNotaFutura::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean): super(dummy)
}
