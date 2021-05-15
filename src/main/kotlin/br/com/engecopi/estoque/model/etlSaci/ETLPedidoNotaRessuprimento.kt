package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.PedidoNotaRessuprimento
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLPedidoNotaRessuprimento : ETL<PedidoNotaRessuprimento>() {
  override val sqlDelete: String
    get() = "DELETE FROM t_pedido_nota_ressuprimento WHERE id =:id"
  override val sqlInsert: String
    get() = """INSERT IGNORE INTO t_pedido_nota_ressuprimento(id, storenoPedido, ordno, storenoNota, nfno, nfse, numero,
      | dataPedido, dataNota)
      | VALUE(:id, :storenoPedido, :ordno, :storenoNota, :nfno, :nfse, :numero, :dataPedido, :dataNota)
    """.trimMargin()
  override val sqlUpdate: String
    get() = """UPDATE t_pedido_nota_ressuprimento
      |SET storenoNota = :storenoNota,
      |    nfno        = :nfno,
      |    nfse        = :nfse,
      |    numero      = :numero
      |WHERE id =:id
    """.trimMargin()

  companion object : ETLThread<PedidoNotaRessuprimento>(ETLPedidoNotaRessuprimento(), 60) {
    val sql = "SELECT * FROM t_pedido_nota_ressuprimento"

    override fun getSource() = saci.findPedidoNota()

    override fun getTarget() = DB.findDto(PedidoNotaRessuprimento::class.java, sql).findList()
  }
}