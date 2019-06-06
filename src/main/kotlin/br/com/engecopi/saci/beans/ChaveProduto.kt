package br.com.engecopi.saci.beans

import br.com.engecopi.utils.lpad

class ChaveProduto(val prdno: String, val grade: String) {
  val codigo
    get() = prdno.lpad(16, " ")
}