package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import io.ebean.DB

class TransferenciaAutomatica(
  id: String,
  val storeno: Int,
  val pdvno: Int,
  val xano: Int,
  val data: Int,
  val storenoFat: Int,
  val nffat: String,
  val storenoTransf: Int,
  val nftransf: String
                             ): EntryID(id) {
  override val chave: String
    get() = "$storenoFat$nffat$storenoTransf$nftransf"
  val numeroTransf get() = "$storenoTransf$nftransf"
  val numeroFat get() = "$storenoFat$nffat"
  
  companion object {
    fun notaFutura(
      lojaTransferencia: Int?, numeroSerieTransferencia: String?
                  ): TransferenciaAutomatica? {
      lojaTransferencia ?: return null
      numeroSerieTransferencia ?: return null
      val sql = """select * from t_transferencia_automatica
        |where storenoTransf = $lojaTransferencia
        |  AND nftransf = '$numeroSerieTransferencia'
      """.trimMargin()
      return DB.findDto(TransferenciaAutomatica::class.java, sql)
        .findList()
        .firstOrNull()
    }
    
    fun notaTransfencia(lojaFatura: Int?, numeroSerieFatura: String?): TransferenciaAutomatica? {
      lojaFatura ?: return null
      numeroSerieFatura ?: return null
      val sql = """select * from t_transferencia_automatica
        |where storenoFat = $lojaFatura
        |  AND nffat = '$numeroSerieFatura'
      """.trimMargin()
      return DB.findDto(TransferenciaAutomatica::class.java, sql)
        .findList()
        .firstOrNull()
    }
  }
}