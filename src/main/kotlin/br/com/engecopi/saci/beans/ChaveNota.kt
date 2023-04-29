package br.com.engecopi.saci.beans

data class ChaveNota(val storeno: Int, val nfno: String, val nfse: String) {
  fun numeroSerie(): String {
    return if (nfse.isBlank()) nfno
    else "$nfno/$nfse"
  }
}