package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.Nota.Find
import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.utils.localDate
import io.ebean.DB

class EntregaFutura(id: String,
                    val storeno: Int,
                    val ordno: Int,
                    val numero_venda: String,
                    val nfno_venda: Int,
                    val nfse_venda: String,
                    val numero_entrega: String,
                    val nfno_entrega: Int,
                    val nfse_entrega: String,
                    val nfekey_entrega: String?): EntryID(id) {
  override val chave: String
    get() = "$numero_entrega$nfno_entrega$nfse_entrega$nfekey_entrega"
  val dataEntrega: Int?
    get() {
      val notaSaci = Find.findNotaSaidaSaci(storeno, "$nfno_entrega/$nfse_entrega").firstOrNull() ?: return null
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
          NotaBaixaFatura(it.storeno, it.numero_entrega, it.dataEntrega?.localDate())
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
          NotaBaixaFatura(it.storeno, it.numero_venda, it.dataEntrega?.localDate())
        }
    }
  }
}