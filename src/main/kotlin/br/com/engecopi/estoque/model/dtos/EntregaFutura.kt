package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.Nota.Find
import br.com.engecopi.estoque.model.etlSaci.EntryID
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
  val numeroEntrega get() = if(numero_entrega == "" || numero_entrega == "0") "" else "$storeno$numero_entrega"
  val dataEntrega: Int?
    get() {
      val notaSaci = Find.findNotaSaidaSaci(storeno, "$nfno_entrega/$nfse_entrega").firstOrNull() ?: return null
      return notaSaci.dtEmissao
    }
  
  companion object {
    fun entrega(storeno: Int?, numeroVenda: String?): EntregaFutura? {
      numeroVenda ?: return null
      storeno ?: return null
      val sql = """select * from t_entrega_futura
        |where storeno = $storeno
        |  AND numero_venda = '$numeroVenda'
      """.trimMargin()
      return DB.findDto(EntregaFutura::class.java, sql)
        .findList()
        .firstOrNull()
    }
  
    fun notaFutura(storeno: Int?, numero: String?): EntregaFutura? {
      numero ?: return null
      storeno ?: return null
      val sql = """select * from t_entrega_futura
        |where storeno = $storeno
        |  AND numero_entrega = '$numero'
      """.trimMargin()
      return DB.findDto(EntregaFutura::class.java, sql)
        .findList()
        .firstOrNull()
    }
  }
}