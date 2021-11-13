package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.utils.localDate
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
  
  companion object {
    fun notaFatura(lojaTransferencia: Int?, numeroSerieTransferencia: String?): List<NotaBaixaFatura> {
      lojaTransferencia ?: return emptyList()
      numeroSerieTransferencia ?: return emptyList()
      val sql = """select * from t_transferencia_automatica
        |where storenoTransf = :lojaTransferencia
        |  AND nftransf = :numeroSerieTransferencia
      """.trimMargin()
      return DB.findDto(TransferenciaAutomatica::class.java, sql)
        .setParameter("lojaTransferencia", lojaTransferencia)
        .setParameter("numeroSerieTransferencia", numeroSerieTransferencia)
        .findList()
        .map {nf ->
          NotaBaixaFatura(nf.storenoFat,
                          nf.nffat,
                          nf.data.localDate())
        }
    }
  
    fun notaBaixa(lojaFatura: Int?, numeroSerieFatura: String?): List<NotaBaixaFatura> {
      lojaFatura ?: return emptyList()
      numeroSerieFatura ?: return emptyList()
      val sql = """select * from t_transferencia_automatica
        |where storenoFat = :lojaFatura
        |  AND nffat = :numeroSerieFatura
      """.trimMargin()
      return DB.findDto(TransferenciaAutomatica::class.java, sql)
        .setParameter("lojaFatura", lojaFatura)
        .setParameter("numeroSerieFatura", numeroSerieFatura)
        .findList()
        .map {nf ->
          NotaBaixaFatura(nf.storenoTransf,
                          nf.nftransf,
                          nf.data.localDate())
        }
    }
  }
}