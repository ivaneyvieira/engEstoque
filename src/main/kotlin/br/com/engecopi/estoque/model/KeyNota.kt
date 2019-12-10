package br.com.engecopi.estoque.model

import br.com.engecopi.utils.mid

class KeyNota(val key: String) {
  val storeno
    get() = if(key.isNotEmpty()) key.mid(0, 1).toIntOrNull() else 0
  val numeroSerie
    get() = if(key.length > 1) key.mid(1) else ""
  val numero
    get() = numeroSerie.split("/").getOrNull(0) ?: ""
  val serie
    get() = numeroSerie.split("/").getOrNull(1) ?: ""
  val loja
    get() = Loja.findLoja(storeno)
}