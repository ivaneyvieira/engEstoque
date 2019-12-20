package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.KeyNota
import java.time.LocalDate

data class NotaBaixaFatura(val storeno: Int, val numero: String, val data: LocalDate?) {
  fun keyNota(): KeyNota? {
    return if(numero == "0" || numero == "") null
    else KeyNota("$storeno$numero")
  }
}

