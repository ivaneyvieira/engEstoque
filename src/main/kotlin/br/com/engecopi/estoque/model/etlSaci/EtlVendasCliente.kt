package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.saci.saci
import io.ebean.DB

object EtlVendasCliente {
  var listenerInsert: (VendasCaixa) -> Unit = {}

  fun update() {
    val source = saci.findVendasCaixa()
    val target = DB.findDto(VendasCaixa::class.java,
                            "select id, storeno, nfno, nfse, prdno, grade, qtty from t_vendas_caixa")
      .findList()
    val etl = ETLVendasCaixa(source.sortedBy {it.id}, target.sortedBy {it.id})
    etl.listenerInsert = listenerInsert

    etl.execute()
  }

  val thread = Thread {
    while(true) {
      update()
      Thread.sleep(10000)
    }
  }

  fun start() {
    thread.start()
  }

  fun stop() {
    thread.interrupt()
  }
}

class ETLVendasCaixa(source: List<VendasCaixa>, target: List<VendasCaixa>): ETL<VendasCaixa>(source, target) {
  override fun deleteTarget(bean: VendasCaixa) {
    val sql = "DELETE FROM t_vendas_caixa WHERE id = :id"
    DB.sqlUpdate(sql)
      .setParameter("id", bean.id)
      .execute()
  }

  override fun insertTarget(bean: VendasCaixa) {
    val sql = """
INSERT INTO t_vendas_caixa(id, storeno, nfno, nfse, prdno, grade, qtty)
VALUES(:id, :storeno, :nfno, :nfse, :prdno, :grade, :qtty)
    """.trimIndent()
    DB.sqlUpdate(sql)
      .setParameter("id", bean.id)
      .setParameter("storeno", bean.storeno)
      .setParameter("nfno", bean.nfno)
      .setParameter("nfse", bean.nfse)
      .setParameter("prdno", bean.prdno)
      .setParameter("grade", bean.grade)
      .setParameter("qtty", bean.qtty)
      .execute()
  }

  override fun updateTarget(bean: VendasCaixa) {
    val sql = """
      UPDATE t_vendas_caixa 
SET  storeno=:storeno, nfno=:nfno, nfse=:nfse, prdno=:prdno, grade=:grade, qtty=:qtty
WHERE id = :id
    """.trimIndent()
    DB.sqlUpdate(sql)
      .setParameter("id", bean.id)
      .setParameter("storeno", bean.storeno)
      .setParameter("nfno", bean.nfno)
      .setParameter("nfse", bean.nfse)
      .setParameter("prdno", bean.prdno)
      .setParameter("grade", bean.grade)
      .setParameter("qtty", bean.qtty)
      .execute()
  }
}
