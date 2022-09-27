package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.utils.localDate

class NotaSaciInfo(
  val invno: Int,
  val storeno: Int,
  val numero: String,
  val serie: String,
  val date: Int,
  val dtEmissao: Int,
  val cancelado: Boolean,
  val tipo: String,
  val area: String,
                  ) {
  val numeroSerie
    get() = if (serie.isBlank()) numero else "$numero/$serie"
  val localDate
    get() = date.localDate()
  val localDtEmissao
    get() = dtEmissao.localDate()
  val tipoNota
    get() = TipoNota.value(tipo)
}