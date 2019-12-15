package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.EntregaFutura
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLEntregaFutura: ETL<EntregaFutura>() {
  override val sqlDelete = "DELETE FROM t_entrega_futura where id = :id"
  override val sqlInsert = """
    INSERT INTO t_entrega_futura(id, storeno, ordno, numero_venda, nfno_venda, nfse_venda, 
                                 numero_entrega, nfno_entrega, nfse_entrega, nfekey_entrega) 
    VALUES(:id, :storeno, :ordno, :numero_venda, :nfno_venda, :nfse_venda, 
           :numero_entrega, :nfno_entrega, :nfse_entrega, :nfekey_entrega)
  """.trimIndent()
  override val sqlUpdate = """
    UPDATE t_entrega_futura 
    SET  numero_entrega=:numero_entrega, 
         nfno_entrega=:nfno_entrega, 
         nfse_entrega=:nfse_entrega,
         nfekey_entrega=:nfekey_entrega
    WHERE id = :id
  """.trimIndent()

  companion object: ETLThread<EntregaFutura>(ETLEntregaFutura(), 60) {
    val sql
      get() = """select id, storeno, ordno, numero_venda, nfno_venda, nfse_venda, 
                        numero_entrega, nfno_entrega, nfse_entrega, nfekey_entrega
      |FROM t_entrega_futura
      |""".trimMargin()
  
    override fun getSource() = saci.findEntregaFutura()
  
    override fun getTarget() = DB.findDto(EntregaFutura::class.java, sql).findList()
  }
}