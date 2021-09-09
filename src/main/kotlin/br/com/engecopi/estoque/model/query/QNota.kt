package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.LancamentoOrigem
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.query.assoc.QAssocItemNota
import br.com.engecopi.estoque.model.query.assoc.QAssocLoja
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import io.ebean.Database
import io.ebean.typequery.*

/**
 * Query bean for Nota.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QNota : TQRootBean<Nota, QNota> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QNota(true)
  }

  lateinit var id: PLong<QNota>
  lateinit var createdAt: PLocalDateTime<QNota>
  lateinit var updatedAt: PLocalDateTime<QNota>
  lateinit var version: PInteger<QNota>
  lateinit var numero: PString<QNota>
  lateinit var numeroEntrega: PString<QNota>
  lateinit var tipoMov: PEnum<QNota, TipoMov>
  lateinit var tipoNota: PEnum<QNota, TipoNota>
  lateinit var rota: PString<QNota>
  lateinit var fornecedor: PString<QNota>
  lateinit var cliente: PString<QNota>
  lateinit var lancamento: PLocalDate<QNota>
  lateinit var data: PLocalDate<QNota>
  lateinit var dataEmissao: PLocalDate<QNota>
  lateinit var hora: PLocalTime<QNota>
  lateinit var observacao: PString<QNota>
  lateinit var loja: QAssocLoja<QNota>
  lateinit var itensNota: QAssocItemNota<QNota>
  lateinit var sequencia: PInteger<QNota>
  lateinit var usuario: QAssocUsuario<QNota>
  lateinit var maxSequencia: PInteger<QNota>
  lateinit var lancamentoOrigem: PEnum<QNota, LancamentoOrigem>

  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(Nota::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(Nota::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
