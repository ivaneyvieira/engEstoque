package br.com.engecopi.estoque.background

import br.com.engecopi.estoque.model.etlSaci.ETLDadosProdutosSaci
import br.com.engecopi.estoque.model.etlSaci.ETLEntregaFutura
import br.com.engecopi.estoque.model.etlSaci.ETLPedidos
import br.com.engecopi.estoque.model.etlSaci.ETLTransferenciaAutomatica

fun main() {
  val home = System.getenv("HOME")
  val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
  System.setProperty("ebean.props.file", fileName)

  ETLPedidos.start()
  ETLEntregaFutura.start()
  ETLTransferenciaAutomatica.start()
  ETLDadosProdutosSaci.start()
}