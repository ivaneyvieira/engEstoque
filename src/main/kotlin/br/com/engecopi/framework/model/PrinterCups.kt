package br.com.engecopi.framework.model

import br.com.engecopi.framework.model.PrinterType.LOCAL
import br.com.engecopi.framework.model.PrinterType.REMOTE
import br.com.engecopi.utils.NetworkUtils
import org.cups4j.CupsClient
import org.cups4j.CupsPrinter
import org.cups4j.PrintJob

open class PrinterCups(val host: String, val port: Int, val userName: String, val localHost: String) {
  private val cupsClient = CupsClient(host, port, userName)
  private val printers
    get() = cupsClient.printers.toList()
  private val printerRemote
    get() = printers.filter {it.location != ""}.map {cupsPrinter ->
      PrinterInfo.newInstance(cupsPrinter, REMOTE)
    }
  private val printerLocal
    get() = printers.filter {it.printerURL.host == localHost}.map {cupsPrinter ->
      PrinterInfo.newInstance(cupsPrinter, LOCAL)
        .copy(location = "Local ${cupsPrinter.name}")
    }
  val printersInfo
    get() = printerRemote
  
  fun printerExists(printerName: String): Boolean {
    val impressoras = printers
    return impressoras.any {it.name == printerName}
  }
  
  fun findPrinter(printerName: String): CupsPrinter? {
    val remote = printerRemote
    val urlHost =
      remote.firstOrNull {it.name == printerName}
        ?.urlHost
    return if(NetworkUtils.isHostReachable(urlHost))
      printers.firstOrNull {it.name == urlHost}
    else {
      val local = printerLocal.firstOrNull()
      if(NetworkUtils.isHostReachable(local?.urlHost))
        printers.firstOrNull {it.name == local?.name}
      else null
    }
  }
  
  @Throws(ECupsPrinter::class)
  fun CupsPrinter.printText(text: String, resultMsg: (String) -> Unit = {}) {
    val job = PrintJob.Builder(text.toByteArray())
      .build()
    try {
      val result = print(job)
      resultMsg("Job ${result.jobId}: ${result.resultDescription} : ${result.resultMessage}")
    } catch(e: Exception) {
      throw ECupsPrinter("Erro de impressão")
    }
  }
  
  fun CupsPrinter.printerTeste() {
    printText(etiqueta)
  }
  
  fun CupsPrinter.isHostReachable(): Boolean {
    val host = this.printerURL.host
    return NetworkUtils.isHostReachable(host)
  }
  
  @Throws(ECupsPrinter::class)
  fun printCups(impressora: String, text: String, resultMsg: (String) -> Unit = {}) {
    val printer =
      findPrinter(impressora)
      ?: throw ECupsPrinter("Impressora $impressora não está configurada no sistema operacional")
    printer.printText(text, resultMsg)
  }
  
  private val etiqueta = """
    |^XA
    |^FT20,070^A0N,70,50^FH^FDNF ENTRADA:1212^FS
    |^FT600,070^A0N,70,50^FH^FD30/06/18^FS
    |^FT20,140^A0N,70,50^FH^FDPRODUTO:000019^FS
    |^FT400,140^A0N,70,50^FH^FD - ^FS
    |^FT20,210^A0N,70,50^FH^FDTGR  SD ADA CT  20X012^FS
    |^FT20,280^A0N,70,50^FH^FDPALLET COM: 5CXS^FS
    |^FT20,350^A0N,70,50^FH^FDENTRADA: 1/5 PALLET^FS
    |^FT20,420^A0N,70,50^FH^FDESTOQUE: 1/5PALLET^FS
    |^FT220,650^A0N,250,300^FH^FD1^FS
    |^FO220,700^BY1^BCN,50,Y,N,N^FD000019  5 1/5^FS
    |^XZ""".trimMargin()
}

data class PrinterInfo(val name: String, val location: String, val description: String,
                       val type: PrinterType) {
  val urlHost
    get() = location
  
  companion object {
    fun newInstance(printer: CupsPrinter, printerType: PrinterType) = PrinterInfo(name = printer.name,
                                                                                  location = printer.location,
                                                                                  description = printer.description,
                                                                                  type = printerType)
  }
}

enum class PrinterType {
  REMOTE,
  LOCAL
}