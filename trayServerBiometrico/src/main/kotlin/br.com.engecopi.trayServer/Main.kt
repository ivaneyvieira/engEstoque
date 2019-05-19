package br.com.engecopi.trayServer

import br.com.engecopi.trayServer.Comando.ComparaDigital
import br.com.engecopi.trayServer.Comando.LerDigital
import br.com.engecopi.trayServer.Comando.RegistraDigital
import br.com.engecopi.trayServer.Comando.ResultadoErro
import br.com.engecopi.trayServer.Comando.ResultadoOk
import br.com.engecopi.trayServer.Mensagem.Companion
import br.com.engecopi.trayServer.Mensagem.Companion.erro
import br.com.engecopi.trayServer.Protocolo.PORT
import com.nitgen.SDK.BSP.NBioBSPJNI
import java.awt.AWTException
import java.awt.CheckboxMenuItem
import java.awt.Image
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.io.IOException
import java.io.PrintWriter
import java.net.ServerSocket
import javax.swing.ImageIcon
import java.io.InputStreamReader
import java.io.BufferedReader
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream

fun main(args: Array<String>) {
  val main = Main()
  main.start()
}

class Main {
  var tray: SystemTray? = null
  var trayIcon: TrayIcon? = null
  private var serverSocket: ServerSocket? = null

  init {
    if (!SystemTray.isSupported()) {
      println("SystemTray is not supported")
    } else {
      tray = SystemTray.getSystemTray()
      val imagem = createImage("/img/finger-icon.png", "Tray icon")
      trayIcon = TrayIcon(imagem)
      // Create a pop-up menu components
      trayIcon?.popupMenu = createPopup()
    }
  }

  //Obtain the image URL
  private fun createImage(path: String, description: String): Image? {
    val imageURL = Main::class.java.getResource(path)

    return if (imageURL == null) {
      System.err.println("Resource not found: $path")
      null
    } else {
      tray?.let { tray ->
        val trayIconSize = tray.trayIconSize
        val trayImage = ImageIcon(imageURL, description).image
        trayImage.getScaledInstance(trayIconSize.width,
                                    trayIconSize.height,
                                    Image.SCALE_SMOOTH);
      }
    }
  }

  fun start() {
    try {
      tray?.add(trayIcon)
      startServer()
    } catch (e: AWTException) {
      println("TrayIcon could not be added.")
    }
  }

  fun startServer() {
    try {
      serverSocket = ServerSocket(PORT)
      serverSocket?.use { serverSocket ->
        System.out.println("Server is listening on port $PORT")

        while (!serverSocket.isClosed) {
          println("Accept ....")
          val socket = serverSocket.accept()
          println("New client connected")
          val output = PrintWriter(socket.getOutputStream(), true)
          val input = BufferedReader(InputStreamReader(socket.getInputStream()))
          val template = processa(input.readLine())
          output.println(template)
        }
      }
    } catch (ex: IOException) {
      println("Server exception: " + ex.message)
      ex.printStackTrace()
    }
  }

  private fun processa(text: String): String? {
    return Mensagem.mensagem(text)?.let { mensagem ->
      val retorno = when (mensagem.comando) {
        ComparaDigital  -> comparaDigiral(mensagem.msg)
        RegistraDigital -> registraDigital(mensagem.msg)
        LerDigital      -> lerDigital()
        ResultadoOk     -> Mensagem.erro("Comando Inválido")
        ResultadoErro   -> Mensagem.erro("Comando Inválido")
      }
      return@let retorno.toText()
    }
  }

  private fun lerDigital(): Mensagem {
    return Nitgen { nitgen ->
      val msg = nitgen.captureDigial() ?: return@Nitgen erro("Erro na captura da digital")
      return@Nitgen Mensagem.ok(msg)
    }.execute()
  }

  private fun registraDigital(loginName: String): Mensagem {
    return Nitgen { nitgen ->
      val msg = nitgen.fingerprintEnrollment(loginName) ?: return@Nitgen erro("Registro inválido")
      return@Nitgen Mensagem.ok(msg)
    }.execute()
  }

  private fun comparaDigiral(msg: String): Mensagem {
    val sep = '\t'
    val dig1 = msg.split("$sep").getOrNull(0) ?: ""
    val dig2 =msg.split("$sep").getOrNull(0) ?: ""
    return Nitgen { nitgen ->
      val match = nitgen.verifyMatch(dig1, dig2)
      return@Nitgen Mensagem.ok(match.toString())
    }.execute()
  }

  fun createPopup() = PopupMenu().apply {
    val menuSair = MenuItem("Sair")
    menuSair.addActionListener {
      tray?.remove(trayIcon)
      serverSocket?.close()
    }
    val enable = CheckboxMenuItem("Habilitado")
    add(enable)
    addSeparator()
    add(menuSair)
  }
}