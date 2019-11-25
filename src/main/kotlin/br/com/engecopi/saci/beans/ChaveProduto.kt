package br.com.engecopi.saci.beans

import br.com.astrosoft.utils.lpad

data class ChaveProduto(val prdno: String, val grade: String, val barcode: String, val tipo: String) {
  val codigo
    get() = prdno.lpad(16, " ")
}

fun List<ChaveProduto>.findChave(): List<ChaveProduto> {
  return this.filter {it.barcode.isNotBlank()}
    .distinctBy {it.barcode + "/" + it.prdno + "/" + it.grade}
}