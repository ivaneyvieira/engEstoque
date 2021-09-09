package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.LancamentoOrigem
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.query.QNota
import io.ebean.typequery.*

/**
 * Association query bean for AssocNota.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocNota<R>(name: String, root: R) : TQAssocBean<Nota, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var numero: PString<R>
  lateinit var numeroEntrega: PString<R>
  lateinit var tipoMov: PEnum<R, TipoMov>
  lateinit var tipoNota: PEnum<R, TipoNota>
  lateinit var rota: PString<R>
  lateinit var fornecedor: PString<R>
  lateinit var cliente: PString<R>
  lateinit var lancamento: PLocalDate<R>
  lateinit var data: PLocalDate<R>
  lateinit var dataEmissao: PLocalDate<R>
  lateinit var hora: PLocalTime<R>
  lateinit var observacao: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var itensNota: QAssocItemNota<R>
  lateinit var sequencia: PInteger<R>
  lateinit var usuario: QAssocUsuario<R>
  lateinit var maxSequencia: PInteger<R>
  lateinit var lancamentoOrigem: PEnum<R, LancamentoOrigem>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QNota>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QNota>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QNota>): R {
    return fetchLazyProperties(*properties)
  }
}
