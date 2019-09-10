package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.utils.localDate

class NFSaida(val storeno: Int,
              val numero: String,
              val serie: String,
              val date: Int,
              val dtEmissao: Int,
              val tipo: String,
              val cancelado: Int
             ) {
  val numeroSerie
    get() = if(serie.isBlank()) numero else "$numero/$serie"
  val localDate
    get() = date.localDate()
  val localDtEmissao
    get() = dtEmissao.localDate()
  val boolCancelado
    get() = cancelado == 1
  val tipoNota
    get() = TipoNota.value(tipo)
}