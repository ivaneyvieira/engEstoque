package br.com.engecopi.trayServer

import br.com.engecopi.trayServer.Mensagem.Companion.erro
import com.nitgen.SDK.BSP.NBioBSPJNI
import com.nitgen.SDK.BSP.NBioBSPJNI.FIR_PAYLOAD
import com.nitgen.SDK.BSP.NBioBSPJNI.INPUT_FIR

class Nitgen(val processamento: (Nitgen) -> Mensagem) {
  private val errors = mapOf(0 to "Nenhum erro",
                             1 to "Invalid handle")
  //private var digitalCapturadaStatusDispositivo: Int = 0
  private val bsp: NBioBSPJNI
  private var deviceEnumInfo: NBioBSPJNI.DEVICE_ENUM_INFO? = null
  private var hasError = false
  private var dispositivoAberto = false
  val deviceInfo
    get() = this.deviceEnumInfo?.DeviceInfo?.get(0)
  val deviceNameID: Short?
    get() = deviceInfo?.NameID
  val deviceName: String?
    get() = deviceInfo?.Name
  val deviceInstace: Short?
    get() = deviceInfo?.Instance
  val lastErrorCode: Int
    get() = if (this.hasError)
      this.bsp.GetErrorCode()
    else
      0
  val lastErrorMessage: String
    get() {
      val numErro = this.bsp.GetErrorCode()
      return this.errors[numErro] ?: "Erro desconhecido: $numErro"
    }
  val version: String
    get() = this.bsp.GetVersion()

  init {
    this.deviceEnumInfo = null
    //this.digitalCapturadaStatusDispositivo = 0
    this.bsp = NBioBSPJNI()
    if (checkError())
      throw exception(Nitgen.DEBUG_PREFIX + "Erro ao criar objeto NBioBSPJNI")

    this.deviceEnumInfo = this.bsp.DEVICE_ENUM_INFO()
    this.bsp.EnumerateDevice(this.deviceEnumInfo)

    if (checkError())
      throw exception(Nitgen.DEBUG_PREFIX + "Erro ao enumerar device")
    val n = this.deviceEnumInfo?.DeviceCount ?: 0

    if (n == 0)
      throw exception(Nitgen.DEBUG_PREFIX + "Componente não iniciado")
    else
      println(Nitgen.DEBUG_PREFIX + "Dispositivo Liberado")

    println(Nitgen.DEBUG_PREFIX + "Dispositivo Iniciado com Sucesso")
  }

  private fun exception(msg: String): Exception {
    return Exception(msg)
  }

  fun openDevice(): Boolean {
    val nameID = deviceNameID ?: return false
    val instance = deviceInstace ?: return false
    this.bsp.OpenDevice(nameID, instance)
    this.dispositivoAberto = true
    println(Nitgen.DEBUG_PREFIX + "Dispositivo Liberado")
    return true
  }

  fun closeDevice() {
    val nameID = deviceNameID ?: return
    val instance = deviceInstace ?: return
    this.bsp.CloseDevice(nameID, instance)
    this.dispositivoAberto = false
  }

  fun captureDigial(): String? {
    if (!this.dispositivoAberto)
      throw exception("Dispositivo não está conectado")
    val fingerPlaced = true
    val erroCheck = this.bsp.CheckFinger(fingerPlaced)

    return if (erroCheck == 0) {
      val fir_handle = this.bsp.FIR_HANDLE()
      //Captura a digital
      this.bsp.Capture(fir_handle)
      //Obtem a digital capturada em modo texto
      if (bsp.IsErrorOccured()) {
        throw exception("Erro na captura e/ou dispositivo")
      } else {
        val textSavedFIR = this.bsp.FIR_TEXTENCODE()
        this.bsp.GetTextFIRFromHandle(fir_handle, textSavedFIR)
        textSavedFIR.TextFIR
      }
    } else
      throw exception("Não foi detectada presença de digitais no dispositivo")
  }

  fun fingerprintEnrollment(loginName: String): String? {
    if (!this.dispositivoAberto)
      throw exception("Dispositivo não está conectado")
    val hSavedFIR = bsp.FIR_HANDLE()
    val payload = bsp.FIR_PAYLOAD()
    payload.SetText(loginName)
    bsp.Enroll(hSavedFIR, payload)
    bsp.Capture(hSavedFIR)
    return if (bsp.IsErrorOccured()) {
      throw exception("Erro na captura da digital")
    } else {
      val textSavedFIR = bsp.FIR_TEXTENCODE()
      bsp.GetTextFIRFromHandle(hSavedFIR, textSavedFIR)
      textSavedFIR.TextFIR
    }
  }

  fun verification(textSaved: String?): Boolean {
    textSaved ?: return false
    val inputFIR = bsp.INPUT_FIR()
    val bResult = false
    val textSavedFIR = bsp.FIR_TEXTENCODE()
    textSavedFIR.TextFIR = textSaved
    inputFIR.SetTextFIR(textSavedFIR)
    val erroCode = bsp.Verify(inputFIR, bResult, null)
    return erroCode == 0
  }

  fun verifyMatch(textCaptured: String?, textSaved: String?): Boolean {
    textSaved ?: return false
    textCaptured ?: return false
    val inputFIR1 = bsp.INPUT_FIR()
    val inputFIR2 = bsp.INPUT_FIR()
    val bResult = false
    val payload = bsp.FIR_PAYLOAD()
    val textSavedFIR = bsp.FIR_TEXTENCODE().apply {
      this.TextFIR = textSaved
    }
    val textCapturedFIR = bsp.FIR_TEXTENCODE().apply {
      this.TextFIR = textCaptured
    }
    inputFIR1.SetTextFIR(textSavedFIR)
    inputFIR2.SetTextFIR(textCapturedFIR)
    bsp.VerifyMatch(inputFIR1, inputFIR2, bResult, payload)
    return if (!bsp.IsErrorOccured())
      bResult
    else
      false
  }

  private fun checkError(): Boolean {
    this.hasError = bsp.IsErrorOccured()
    if (this.hasError)
      System.err.println(Nitgen.DEBUG_PREFIX + this.lastErrorMessage)
    return this.hasError
  }

  fun execute(): Mensagem {
    return try {
      openDevice()
      val mensagem = processamento(this)
      closeDevice()
      mensagem
    }catch(e : Throwable){
      erro(e.localizedMessage)
    }
  }

  companion object {
    private val DEBUG_PREFIX = "WINPONTA NITGEN - DEBUG :: "
  }
}

