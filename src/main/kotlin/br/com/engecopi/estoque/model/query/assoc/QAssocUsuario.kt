package br.com.engecopi.estoque.model.query.assoc

import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.query.QUsuario
import io.ebean.typequery.*

/**
 * Association query bean for AssocUsuario.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@TypeQueryBean
class QAssocUsuario<R>(name: String, root: R) : TQAssocBean<Usuario, R>(name, root) {

  lateinit var id: PLong<R>
  lateinit var createdAt: PLocalDateTime<R>
  lateinit var updatedAt: PLocalDateTime<R>
  lateinit var version: PInteger<R>
  lateinit var loginName: PString<R>
  lateinit var loja: QAssocLoja<R>
  lateinit var localizacaoes: PString<R>
  lateinit var notaSeries: PString<R>
  lateinit var impressora: PString<R>
  lateinit var itensNota: QAssocItemNota<R>
  lateinit var admin: PBoolean<R>
  lateinit var estoque: PBoolean<R>
  lateinit var expedicao: PBoolean<R>
  lateinit var painel: PBoolean<R>
  lateinit var configuracao: PBoolean<R>
  lateinit var entregaFutura: PBoolean<R>
  lateinit var etiqueta: PBoolean<R>
  lateinit var ressuprimento: PBoolean<R>
  lateinit var abastecimento: PBoolean<R>

  /**
   * Eagerly fetch this association loading the specified properties.
   */
  fun fetch(vararg properties: TQProperty<QUsuario>): R {
    return fetchProperties(*properties)
  }

  /**
   * Eagerly fetch this association using a 'query join' loading the specified properties.
   */
  fun fetchQuery(vararg properties: TQProperty<QUsuario>): R {
    return fetchQueryProperties(*properties)
  }

  /**
   * Use lazy loading for this association loading the specified properties.
   */
  fun fetchLazy(vararg properties: TQProperty<QUsuario>): R {
    return fetchLazyProperties(*properties)
  }
}
