package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.EntregaFutura
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLEntregaFutura: ETL<EntregaFutura>() {
  override val sqlDelete = "DELETE FROM t_entrega_futura where id = :id"
  override val sqlInsert = """
    INSERT IGNORE INTO t_entrega_futura(id, storenoVenda, numeroVenda, nfnoVenda, nfseVenda,
                                 storenoEntrega, numeroEntrega, nfnoEntrega, nfseEntrega, nfekeyEntrega, dataVenda,
                                 dataEntrega)
    VALUES(:id, :storenoVenda, :numeroVenda, :nfnoVenda, :nfseVenda,
           :storenoEntrega, :numeroEntrega, :nfnoEntrega, :nfseEntrega, :nfekeyEntrega, :dataVenda, :dataEntrega)
  """.trimIndent()
  override val sqlUpdate = """
    UPDATE t_entrega_futura
    SET  storenoEntrega=:storenoEntrega,
         numeroEntrega=:numeroEntrega,
         nfnoEntrega=:nfnoEntrega, 
         nfseEntrega=:nfseEntrega,
         dataEntrega=:dataEntrega,
         nfekeyEntrega=:nfekeyEntrega
    WHERE id = :id
  """.trimIndent()

  companion object: ETLThread<EntregaFutura>(ETLEntregaFutura(), 60) {
    val sql
      get() = "select * FROM t_entrega_futura"
  
    override fun getSource() = saci.findEntregaFutura()
  
    override fun getTarget() = DB.findDto(EntregaFutura::class.java, sql).findList()
  }
}