package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID

class VendasCaixa(
  id: String,
  val storeno: Int,
  val nfno: Int,
  val nfse: String,
  val prdno: String,
  val grade: String,
  val qtty: Double,
                 ) : EntryID(id) {
  override val chave: String
    get() = "$qtty"
}