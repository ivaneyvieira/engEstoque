package br.com.engecopi.estoque.model

import br.com.engecopi.utils.mid

open class KeyNota(val key: String) {
  val storeno
    get() = if(key.isNotEmpty()) key.mid(0, 1).toIntOrNull() else 0
  val numero
    get() = if(key.length > 1) key.mid(1) else ""
  val nfno
    get() = numero.split("/").getOrNull(0) ?: ""
  val nfse
    get() = numero.split("/").getOrNull(1) ?: ""
  val loja
    get() = Loja.findLoja(storeno)
}