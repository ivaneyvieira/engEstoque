package br.com.engecopi.saci.beans

import br.com.engecopi.utils.lpad

class ChaveProduto(val prdno: String, val grade: String, val barcode: String, val tipo: String) {
  val codigo
    get() = prdno.lpad(16, " ")
}

fun List<ChaveProduto>.findChave(): ChaveProduto? {
  val listaPdv = this.filter {it.tipo == "PDV"}
  val listaGrade = this.filter {it.tipo == "GRADE"}
  val listaPrd = this.filter {it.tipo == "PRD"}
  return listaGrade.firstOrNull() /*?: listaPdv.firstOrNull() */?: listaPrd.firstOrNull()
}