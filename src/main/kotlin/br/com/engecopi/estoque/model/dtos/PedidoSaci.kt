package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName

@TableName("t_pedido")
class PedidoSaci(id: String,
                 val rota: String?,
                 val storeno: Int?,
                 val numero: String?,
                 val date: Int?,
                 val clienteName: String?,
                 val abreviacao: String?,
                 val nfno: String?,
                 val nfse: String?,
                 val status: Int?): EntryID(id) {
  override val chave: String
    get() = "$abreviacao$status"
}