package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.Nota.Find
import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.utils.localDate
import io.ebean.DB

class EntregaFutura(id: String,
                    val storenoVenda: Int,
                    val numeroVenda: String,
                    val nfnoVenda: Int,
                    val nfseVenda: String,
                    val storenoEntrega: Int,
                    val numeroEntrega: String,
                    val nfnoEntrega: Int,
                    val nfseEntrega: String,
                    val nfekeyEntrega: String?): EntryID(id) {
  override val chave: String
    get() = "$storenoEntrega$numeroEntrega$nfnoEntrega$nfseEntrega$nfekeyEntrega"
  val dataEntrega: Int?
    get() {
      val notaSaci = Find.findNotaSaidaSaci(storenoEntrega, "$nfnoEntrega/$nfseEntrega").firstOrNull() ?: return null
      return notaSaci.dtEmissao
    }
  
  companion object {
    fun notaBaixa(storeno: Int?, numeroVenda: String?): NotaBaixaFatura? {
      numeroVenda ?: return null
      storeno ?: return null
      val sql = """select * from t_entrega_futura
        |where storeno = :storeno
        |  AND numero_venda = :numeroVenda
      """.trimMargin()
      return DB.findDto(EntregaFutura::class.java, sql)
        .setParameter("storeno", storeno)
        .setParameter("numeroVenda", numeroVenda)
        .findList()
        .firstOrNull()
        ?.let {
          NotaBaixaFatura(it.storenoEntrega, it.numeroEntrega, it.dataEntrega?.localDate())
        }
    }
    
    fun notaFatura(storeno: Int?, numero: String?): NotaBaixaFatura? {
      numero ?: return null
      storeno ?: return null
      val sql = """select * from t_entrega_futura
        |where storeno = :storeno
        |  AND numero_entrega = :numero
      """.trimMargin()
      return DB.findDto(EntregaFutura::class.java, sql)
        .setParameter("storeno", storeno)
        .setParameter("numero", numero)
        .findList()
        .firstOrNull()
        ?.let {
          NotaBaixaFatura(it.storenoEntrega, it.numeroVenda, it.dataEntrega?.localDate())
        }
    }
  }
}