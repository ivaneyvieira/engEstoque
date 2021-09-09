package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.utils.localDate
import io.ebean.DB

class EntregaFutura(
  id: String,
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
  val nfekeyEntrega: String?,
                   ) : EntryID(id) {
  override val chave: String
    get() = "$storenoEntrega$numeroEntrega$nfnoEntrega$nfseEntrega$nfekeyEntrega"

  companion object {
    fun notaBaixa(storeno: Int?, numeroVenda: String?): List<NotaBaixaFatura> {
      numeroVenda ?: return emptyList()
      storeno ?: return emptyList()
      val sql = """select * from t_entrega_futura
        |where storenoVenda = :storeno
        |  AND numeroVenda = :numeroVenda
      """.trimMargin()
      val list =
              DB.findDto(EntregaFutura::class.java, sql)
                .setParameter("storeno", storeno)
                .setParameter("numeroVenda", numeroVenda)
                .findList()
                .map { nf ->
                  NotaBaixaFatura(nf.storenoEntrega, nf.numeroEntrega, nf.dataEntrega.localDate())
                }
      return list
    }

    fun notaFatura(storeno: Int?, numero: String?): List<NotaBaixaFatura> {
      numero ?: return emptyList()
      storeno ?: return emptyList()
      val sql = """select * from t_entrega_futura
        |where storenoEntrega = :storeno
        |  AND numeroEntrega = :numero
      """.trimMargin()
      return DB.findDto(EntregaFutura::class.java, sql)
        .setParameter("storeno", storeno)
        .setParameter("numero", numero)
        .findList()
        .map { nf ->
          NotaBaixaFatura(nf.storenoVenda, nf.numeroVenda, nf.dataVenda.localDate())
        }
    }
  }
}

