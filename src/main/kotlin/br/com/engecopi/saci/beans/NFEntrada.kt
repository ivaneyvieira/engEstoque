package br.com.engecopi.saci.beans

import br.com.engecopi.utils.localDate

data class NFEntrada(val invno: Int,
                     val storeno: Int,
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
}
