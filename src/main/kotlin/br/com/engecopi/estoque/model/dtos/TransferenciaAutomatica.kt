package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName
import br.com.engecopi.utils.localDate
import io.ebean.DB
import io.ebean.DtoQuery

@TableName("t_transferencia_automatica")
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
    private val SQL_BAIXA = """select * from t_transferencia_automatica
        |where storenoFat = :lojaFatura
        |  AND nffat = :numeroSerieFatura
      """.trimMargin()
    private val SQL_FATURA = """select * from t_transferencia_automatica
        |where storenoTransf = :lojaTransferencia
        |  AND nftransf = :numeroSerieTransferencia
      """.trimMargin()
    private val queryBaixa: DtoQuery<TransferenciaAutomatica> =
      DB.findDto(TransferenciaAutomatica::class.java, SQL_BAIXA)
    private val queryfatura: DtoQuery<TransferenciaAutomatica> =
      DB.findDto(TransferenciaAutomatica::class.java, SQL_FATURA)
  
    fun notaFatura(lojaTransferencia: Int?, numeroSerieTransferencia: String?): List<NotaBaixaFatura> {
      lojaTransferencia ?: return emptyList()
      numeroSerieTransferencia ?: return emptyList()
    
      return queryfatura
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
  
      return queryBaixa
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