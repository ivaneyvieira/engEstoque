package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLVendasCaixa : ETL<VendasCaixa>() {
  override val sqlDelete = "DELETE FROM t_vendas_caixa WHERE id = :id"
  override val sqlInsert = """
       INSERT INTO t_vendas_caixa(id, storeno, nfno, nfse, prdno, grade, qtty)
       VALUES(:id, :storeno, :nfno, :nfse, :prdno, :grade, :qtty)
    """.trimIndent()
  override val sqlUpdate = """
      UPDATE t_vendas_caixa 
      SET  qtty=:qtty
      WHERE id = :id
    """.trimIndent()

  companion object : ETLThread<VendasCaixa>(ETLVendasCaixa(), 30) {
    val sql
      get() = "select id, storeno, nfno, nfse, prdno, grade, qtty from t_vendas_caixa"

    override fun getSource() = saci.findVendasCaixa()

    override fun getTarget() = DB.findDto(VendasCaixa::class.java, sql).findList()
  }
}
