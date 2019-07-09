package br.com.engecopi.saci.beans

import br.com.engecopi.utils.lpad

data class ChaveProduto(val prdno: String, val grade: String, val barcode: String, val tipo: String) {
  val codigo
    get() = prdno.lpad(16, " ")
}

fun List<ChaveProduto>.findChave(): List<ChaveProduto> {
  /* val listaPrd2 = this.filter {it.tipo == "PRD2" && it.grade != ""}
   val listaGrade = this.filter {it.tipo == "GRADE" && it.grade != ""}
   val listaPrd = this.filter {it.tipo == "PRD" && it.grade == ""}

   return when {
     listaGrade.isNotEmpty() -> listaGrade
     listaPrd2.isNotEmpty()  -> listaPrd2
     listaPrd.isNotEmpty()   -> listaPrd
     else                    -> emptyList()
   }*/
  return this.filter {it.barcode.isNotBlank()}
    .distinctBy {it.barcode + "/" + it.prdno + "/" + it.grade}
}