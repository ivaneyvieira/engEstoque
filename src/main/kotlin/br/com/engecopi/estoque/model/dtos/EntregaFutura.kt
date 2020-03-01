package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName
import br.com.engecopi.utils.localDate
import io.ebean.DB
import io.ebean.DtoQuery

@TableName("t_entrega_futura")
class EntregaFutura(id: String,
                    val storenoVenda: Int,
                    val numeroVenda: String,
                    val nfnoVenda: Int,
                    val nfseVenda: String,
                    val dataVenda: Int,
                    val storenoEntrega: Int,
                    val numeroEntrega: String,
                    val nfnoEntrega: Int,
                    val nfseEntrega: String,
                    val dataEntrega: Int,
                    val nfekeyEntrega: String?): EntryID(id) {
  override val chave: String
    get() = "$storenoEntrega$numeroEntrega$nfnoEntrega$nfseEntrega$nfekeyEntrega"
  
  companion object {
    private val SQL_FATURA = """select * from t_entrega_futura
        |where storenoEntrega = :storeno
        |  AND numeroEntrega = :numero
      """.trimMargin()
    private val queryFatura: DtoQuery<EntregaFutura> =
      DB.findDto(EntregaFutura::class.java, SQL_FATURA)
    private val SQL_BAIXA = """select * from t_entrega_futura
        |where storenoVenda = :storeno
        |  AND numeroVenda = :numeroVenda
      """.trimMargin()
    private val queryBaixa: DtoQuery<EntregaFutura> =
      DB.findDto(EntregaFutura::class.java, SQL_BAIXA)
  
    fun notaBaixa(storeno: Int?, numeroVenda: String?): List<NotaBaixaFatura> {
      numeroVenda ?: return emptyList()
      storeno ?: return emptyList()
    
      return queryBaixa
        .setParameter("storeno", storeno)
        .setParameter("numeroVenda", numeroVenda)
        .findList()
        .map {nf ->
          NotaBaixaFatura(nf.storenoEntrega,
                          nf.numeroEntrega,
                          nf.dataEntrega.localDate())
        }
    }
    
    fun notaFatura(storeno: Int?, numero: String?): List<NotaBaixaFatura> {
      numero ?: return emptyList()
      storeno ?: return emptyList()
  
      return queryFatura
        .setParameter("storeno", storeno)
        .setParameter("numero", numero)
        .findList()
        .map {nf ->
          NotaBaixaFatura(nf.storenoVenda,
                          nf.numeroVenda,
                          nf.dataVenda.localDate())
        }
    }
  }
}

