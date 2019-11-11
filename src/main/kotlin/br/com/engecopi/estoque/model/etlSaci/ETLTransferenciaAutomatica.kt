package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.TransferenciaAutomatica
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLTransferenciaAutomatica: ETL<TransferenciaAutomatica>() {
  override val sqlDelete = "DELETE FROM t_transferencia_automatica where id = :id"
  override val sqlInsert = """
    |insert into t_transferencia_automatica(id, storeno, pdvno, xano, data, storenoFat, nffat, storenoTransf, nftransf) 
    |values(:id, :storeno, :pdvno, :xano, :data, :storenoFat, :nffat, :storenoTransf, :nftransf)""".trimMargin()
  override val sqlUpdate = """
    |UPDATE t_transferencia_automatica 
    |SET storenoFat=:storenoFat, 
    |    nffat=:nffat, 
    |    storenoTransf=:storenoTransf, 
    |    nftransf=:nftransf 
    |WHERE id = :id""".trimMargin()

  companion object: ETLThread<TransferenciaAutomatica>(ETLTransferenciaAutomatica()) {
    val sql = "SELECT * FROM t_transferencia_automatica"

    override fun getSource() = saci.findTransferenciaAutomatica()

    override fun getTarget() = DB
      .findDto(TransferenciaAutomatica::class.java, sql)
      .findList()
  }
}