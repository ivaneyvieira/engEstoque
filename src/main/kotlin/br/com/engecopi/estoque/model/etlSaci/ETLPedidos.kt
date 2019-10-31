package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.dtos.PedidoSaci
import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.estoque.model.etlSaci.ETLVendasCaixa.Companion
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLPedidos(): ETL<PedidoSaci>() {
  override val sqlDelete = "DELETE FROM t_pedido where id = :id"
  override val sqlInsert = """
      INSERT INTO t_pedido(id, rota, storeno, numero, date, clienteName, abreviacao, nfno, nfse, status)
      VALUES(:id, :rota, :storeno, :numero, :date, :clienteName, :abreviacao, :nfno, :nfse, :status)
    """.trimIndent()
  override val sqlUpdate = """
      UPDATE t_pedido 
      SET  rota=:rota,
           storeno=:storeno, 
           numero=:numero, 
           date=:date, 
           clienteName=:clienteName, 
           abreviacao=:abreviacao, 
           nfno=:nfno, 
           nfse=:nfse, 
           status=:status
      WHERE id = :id
    """.trimIndent()

  companion object: ETLThread<PedidoSaci>(ETLPedidos()) {
    val sql
      get() = """select id, rota, storeno, numero, date, clienteName, abreviacao, nfno, nfse, status 
      |from t_pedido
      |where storeno = ${lojaDefault.numero}
      |""".trimMargin()

    override fun getSource() = saci.findPedidoTransferencia(lojaDefault.numero)

    override fun getTarget() = DB
      .findDto(PedidoSaci::class.java, sql)
      .findList()
  }
}


