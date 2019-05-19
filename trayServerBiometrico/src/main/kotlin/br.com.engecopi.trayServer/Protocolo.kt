package br.com.engecopi.trayServer

import br.com.engecopi.trayServer.Comando.ResultadoErro
import br.com.engecopi.trayServer.Comando.ResultadoOk
import br.com.engecopi.trayServer.Protocolo.SEPARATOR

object Protocolo {
  const val PORT = 20999
  const val SEPARATOR = '\t'
}

enum class Comando {
  RegistraDigital,
  LerDigital,
  ComparaDigital,
  ResultadoOk,
  ResultadoErro
}

data class Mensagem(val comando: Comando, val msg: String) {
  companion object {
    fun mensagem(text: String): Mensagem? {
      val comandoStr = text.split(SEPARATOR).getOrNull(0)
      val messagem = text.split(SEPARATOR).getOrNull(1) ?: ""
      return Comando.values().find { it.toString() == comandoStr }?.let { comando ->
        Mensagem(comando = comando, msg = messagem)
      }
    }

    fun erro(msg: String): Mensagem {
      return Mensagem(ResultadoErro, msg)
    }

    fun ok(msg: String): Mensagem {
      return Mensagem(ResultadoOk, msg)
    }
  }

  fun toText() : String {
    return "$comando$SEPARATOR$msg"
  }
}