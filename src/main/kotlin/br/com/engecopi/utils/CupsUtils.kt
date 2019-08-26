package br.com.engecopi.utils

import org.cups4j.CupsClient
import org.cups4j.CupsPrinter
import org.cups4j.PrintJob

object CupsUtils {
  private val cupsClient = CupsClient("192.168.1.14", 631, "root")
  private val printers
    get() = cupsClient.printers.toList()

  fun printerExists(printerName: String): Boolean {
    val impressoras = printers
    return impressoras.any {it.name == printerName}
  }

  fun printer(printerName: String) = printers.firstOrNull {it.name == printerName}

  fun CupsPrinter.printText(text: String, resultMsg: (String) -> Unit = {}) {
    val job = PrintJob.Builder(text.toByteArray())
      .build()
    val result = print(job)
    resultMsg("Job ${result.jobId}: ${result.resultDescription} : ${result.resultMessage}")
  }

  fun CupsPrinter.printerTeste() {
    printText(etiqueta)
  }

  private const val etiqueta = "^XA\n" + "^FT20,070^A0N,70,50^FH^FDNF ENTRADA:1212^FS\n" + "^FT600,070^A0N,70," +
                               "50^FH^FD30/06/18^FS\n" + "^FT20,140^A0N,70,50^FH^FDPRODUTO:000019^FS\n" + "^FT400,140^A0N,70,50^FH^FD - ^FS\n" + "^FT20,210^A0N,70,50^FH^FDTGR  SD ADA CT  20X012^FS\n" + "^FT20,280^A0N,70,50^FH^FDPALLET COM: 5CXS^FS\n" + "^FT20,350^A0N,70,50^FH^FDENTRADA: 1/5 PALLET^FS\n" + "^FT20,420^A0N,70,50^FH^FDESTOQUE: 1/5PALLET^FS\n" + "^FT220,650^A0N,250,300^FH^FD1^FS\n" + "^FO220,700^BY1^BCN,50,Y,N,N^FD000019  5 1/5^FS\n" + "^XZ"
}

