package br.com.engecopi.utils

class Valor(val value: Int) {
  operator fun plus(b: Valor) = Valor(value + b.value)
  fun print() {
    print("Valor é $value")
  }
}

fun main() {
  val a = Valor(1)
  val b = Valor(2)
  val c = a + b
  c.print()
}
