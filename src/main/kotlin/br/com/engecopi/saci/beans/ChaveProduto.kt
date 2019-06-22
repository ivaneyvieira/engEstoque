package br.com.engecopi.saci.beans

import br.com.engecopi.utils.lpad

class ChaveProduto(val prdno: String, val grade: String, val barcode: String, val tipo: String) {
  val codigo
    get() = prdno.lpad(16, " ")
}

fun List<ChaveProduto>.findChave(): ChaveProduto? {
  val listaPrd2 = this.filter {it.tipo == "PDV2" && it.grade != ""}
  val listaGrade = this.filter {it.tipo == "GRADE" && it.grade != ""}
  val listaPrd = this.filter {it.tipo == "PRD" && it.grade == ""}
  return listaGrade.firstOrNull() ?: listaPrd2.firstOrNull()  ?: listaPrd.firstOrNull()
}