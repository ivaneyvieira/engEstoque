package br.com.engecopi.clientBio

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.trayServer.Mensagem
import br.com.engecopi.trayServer.Protocolo.PORT
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClienteBio {
  private var clientSocket: Socket? = null
  private var output: PrintWriter? = null
  private var input: BufferedReader? = null

  private fun startConnection() {
    clientSocket = Socket(RegistryUserInfo.endereco, PORT)
    output = PrintWriter(clientSocket?.getOutputStream(), true)
    input = BufferedReader(InputStreamReader(clientSocket?.getInputStream()))
  }

  private fun sendText(msg: String): String {
    output?.println(msg)
    return input?.readLine() ?: ""
  }

  fun stopConnection() {
    input?.close()
    output?.close()
    clientSocket?.close()
  }

  fun sendMessage(message : Mensagem) : Mensagem? {
    val retorno = sendText(message.toText())
    return Mensagem.mensagem(retorno)
  }
}