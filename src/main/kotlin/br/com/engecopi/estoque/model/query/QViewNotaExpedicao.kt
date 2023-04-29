package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewNotaExpedicao
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import io.ebean.Database
import io.ebean.typequery.*

/**
 * Query bean for ViewNotaExpedicao.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QViewNotaExpedicao : TQRootBean<ViewNotaExpedicao, QViewNotaExpedicao> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QViewNotaExpedicao(true)
  }

  lateinit var id: PLong<QViewNotaExpedicao>
  lateinit var createdAt: PLocalDateTime<QViewNotaExpedicao>
  lateinit var updatedAt: PLocalDateTime<QViewNotaExpedicao>
  lateinit var version: PInteger<QViewNotaExpedicao>
  lateinit var nota: QAssocNota<QViewNotaExpedicao>
  lateinit var numero: PString<QViewNotaExpedicao>
  lateinit var tipoMov: PEnum<QViewNotaExpedicao, TipoMov>
  lateinit var tipoNota: PEnum<QViewNotaExpedicao, TipoNota>
  lateinit var rota: PString<QViewNotaExpedicao>
  lateinit var fornecedor: PString<QViewNotaExpedicao>
  lateinit var cliente: PString<QViewNotaExpedicao>
  lateinit var data: PLocalDate<QViewNotaExpedicao>
  lateinit var dataEmissao: PLocalDate<QViewNotaExpedicao>
  lateinit var lancamento: PLocalDate<QViewNotaExpedicao>
  lateinit var hora: PLocalTime<QViewNotaExpedicao>
  lateinit var observacao: PString<QViewNotaExpedicao>
  lateinit var loja: QAssocLoja<QViewNotaExpedicao>
  lateinit var sequencia: PInteger<QViewNotaExpedicao>
  lateinit var usuario: QAssocUsuario<QViewNotaExpedicao>
  lateinit var abreviacao: PString<QViewNotaExpedicao>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ViewNotaExpedicao::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ViewNotaExpedicao::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
