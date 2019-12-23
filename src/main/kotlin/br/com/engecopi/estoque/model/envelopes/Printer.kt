package br.com.engecopi.estoque.model.envelopes

data class Printer(val nome: String) {
  companion object {
    val VAZIA = Printer("")
  }
}