package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID

class TransferenciaAutomatica(
  id: String,
  val storeno: Int,
  val pdvno: Int,
  val xano: Int,
  val data: Int,
  val storenoFat: Int,
  val nffat: String,
  val storenoTransf: Int,
  val nftransf: String
                             ): EntryID(id) {
  override val chave: String
    get() = "$storenoFat$nffat$storenoTransf$nftransf"
}