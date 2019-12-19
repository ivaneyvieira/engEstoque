package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.etlSaci.EntryID
import io.ebean.DB

class TransferenciaAutomatica(id: String,
                              val storeno: Int,
                              val pdvno: Int,
                              val xano: Int,
                              val data: Int,
                              val storenoFat: Int,
                              val nffat: String,
                              val storenoTransf: Int,
                              val nftransf: String): EntryID(id) {
  override val chave: String
    get() = "$storenoFat$nffat$storenoTransf$nftransf"
  val numeroTransf get() = if(nftransf == "" || nftransf == "0") "" else "$storenoTransf$nftransf"
  val numeroFat get() = if(nffat == "" || nffat == "0") "" else "$storenoFat$nffat"
  
  companion object {
    fun notaFatura(lojaTransferencia: Int?, numeroSerieTransferencia: String?): KeyNota? {
      lojaTransferencia ?: return null
      numeroSerieTransferencia ?: return null
      val sql = """select * from t_transferencia_automatica
        |where storenoTransf = :lojaTransferencia
        |  AND nftransf = :numeroSerieTransferencia
      """.trimMargin()
      return DB.findDto(TransferenciaAutomatica::class.java, sql)
        .setParameter("lojaTransferencia", lojaTransferencia)
        .setParameter("numeroSerieTransferencia", numeroSerieTransferencia)
        .findList()
        .firstOrNull()
        ?.let {
          KeyNota("${it.storenoFat}${it.nffat}")
        }
    }
  
    fun notaBaixa(lojaFatura: Int?, numeroSerieFatura: String?): KeyNota? {
      lojaFatura ?: return null
      numeroSerieFatura ?: return null
      val sql = """select * from t_transferencia_automatica
        |where storenoFat = :lojaFatura
        |  AND nffat = :numeroSerieFatura
      """.trimMargin()
      return DB.findDto(TransferenciaAutomatica::class.java, sql)
        .setParameter("lojaFatura", lojaFatura)
        .setParameter("numeroSerieFatura", numeroSerieFatura)
        .findList()
        .firstOrNull()
        ?.let {
          KeyNota("${it.storenoTransf}${it.nftransf}")
        }
    }
  }
}