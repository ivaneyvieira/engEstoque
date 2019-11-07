package br.com.engecopi.estoque.viewmodel

data class PacoteImpressao(val impressora: String, val text: String) {
  companion object {
    fun empty() = PacoteImpressao("", "")
  }
}