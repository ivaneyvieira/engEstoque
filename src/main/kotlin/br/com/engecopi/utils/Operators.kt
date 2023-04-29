package br.com.engecopi.utils

fun <T : Any> T.IN(vararg itens: T): Boolean {
  val list = itens.toList()
  return list.contains(this)
}