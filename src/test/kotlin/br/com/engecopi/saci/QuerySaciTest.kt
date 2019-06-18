package br.com.engecopi.saci

import org.junit.jupiter.api.Test

class QuerySaciTest {

  init {
    val home = System.getenv("HOME")
    val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
    System.setProperty("ebean.props.file", fileName)
    println("##################### $fileName")
  }

  @Test
  fun notaSaida() {
    //saci.findNotaSaida(4, "1233", "1", true)
  }

  @Test
  fun pedidos() {
  //  saci.findNotaSaida(4, "1233", "", true)
  }

  @Test
  fun notaEntrada() {
   // saci.findNotaEntrada(4, "1233", "1", true)
  }
}