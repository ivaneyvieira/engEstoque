package br.com.engecopi.estoque.model.query

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.query.assoc.QAssocEtiqueta
import br.com.engecopi.estoque.model.query.assoc.QAssocNota
import br.com.engecopi.estoque.model.query.assoc.QAssocProduto
import br.com.engecopi.estoque.model.query.assoc.QAssocUsuario
import io.ebean.Database
import io.ebean.typequery.PBoolean
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
 * Query bean for ItemNota.
 * 
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QItemNota : TQRootBean<ItemNota, QItemNota> {

  companion object {
    /**
     * shared 'Alias' instance used to provide
     * properties to select and fetch clauses
     */
    val _alias = QItemNota(true)
  }

  lateinit var id: PLong<QItemNota>
  lateinit var createdAt: PLocalDateTime<QItemNota>
  lateinit var updatedAt: PLocalDateTime<QItemNota>
  lateinit var version: PInteger<QItemNota>
  lateinit var data: PLocalDate<QItemNota>
  lateinit var hora: PLocalTime<QItemNota>
  lateinit var quantidade: PInteger<QItemNota>
  lateinit var quantidadeSaci: PInteger<QItemNota>
  lateinit var produto: QAssocProduto<QItemNota>
  lateinit var nota: QAssocNota<QItemNota>
  lateinit var etiqueta: QAssocEtiqueta<QItemNota>
  lateinit var usuario: QAssocUsuario<QItemNota>
  lateinit var saldo: PInteger<QItemNota>
  lateinit var impresso: PBoolean<QItemNota>
  lateinit var localizacao: PString<QItemNota>
  lateinit var status: PEnum<QItemNota,StatusNota>
  lateinit var codigoBarraCliente: PString<QItemNota>
  lateinit var codigoBarraConferencia: PString<QItemNota>
  lateinit var codigoBarraConferenciaBaixa: PString<QItemNota>
  lateinit var codigoBarraEntrega: PString<QItemNota>


  /**
   * Construct with a given Database.
   */
  constructor(database: Database) : super(ItemNota::class.java, database)

  /**
   * Construct using the default Database.
   */
  constructor() : super(ItemNota::class.java)

  /**
   * Construct for Alias.
   */
  private constructor(dummy: Boolean) : super(dummy)
}
