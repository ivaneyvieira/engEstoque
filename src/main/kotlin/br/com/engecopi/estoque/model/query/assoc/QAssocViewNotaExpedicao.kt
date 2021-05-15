package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewNotaExpedicao
import br.com.engecopi.estoque.model.query.QViewNotaExpedicao
import io.ebean.typequery.*

/**
 * Association query bean for AssocViewNotaExpedicao.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean class QAssocViewNotaExpedicao<R>(name: String, root: R) : TQAssocBean<ViewNotaExpedicao, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var nota: QAssocNota<R>
  lateinit var numero: PString<R>
  lateinit var tipoMov: PEnum<R, TipoMov>
  lateinit var tipoNota: PEnum<R, TipoNota>
  lateinit var rota: PString<R>
  lateinit var fornecedor: PString<R>
  lateinit var cliente: PString<R>
  lateinit var data: PLocalDate<R>
  lateinit var dataEmissao: PLocalDate<R>
  lateinit var lancamento: PLocalDate<R>
  lateinit var hora: PLocalTime<R>
  lateinit var observacao: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var sequencia: PInteger<R>
  lateinit var usuario: QAssocUsuario<R>
  lateinit var abreviacao: PString<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QViewNotaExpedicao>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QViewNotaExpedicao>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QViewNotaExpedicao>): R {
    return fetchLazyProperties(*properties)
  }

}
