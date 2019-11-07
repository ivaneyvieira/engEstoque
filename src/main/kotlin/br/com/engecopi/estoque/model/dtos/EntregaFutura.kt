package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID

class EntregaFutura(
  id: String,
  val storeno: Int,
  val ordno: Int,
  val numero_venda: String,
  val nfno_venda: Int,
  val nfse_venda: String,
  val numero_entrega: String,
  val nfno_entrega: Int,
  val nfse_entrega: String,
  val nfekey_entrega: String?
                   ): EntryID(id) {
  override val chave: String
    get() = "$numero_entrega$nfno_entrega$nfse_entrega$nfekey_entrega"
}