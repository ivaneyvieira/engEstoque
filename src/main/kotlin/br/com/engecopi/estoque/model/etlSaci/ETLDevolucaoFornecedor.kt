package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.dtos.DevolucaoFornecedor
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLDevolucaoFornecedor: ETL<DevolucaoFornecedor>() {
  override val sqlDelete: String
    get() = "DELETE FROM t_devolucao_fornecedor WHERE id = :id"
  override val sqlInsert: String
    get() = """INSERT INTO t_devolucao_fornecedor(id, storeno, pdvno, xano, invno, nfeno, nfese,
              |nfsno, nfsse, localizacao)
              |VALUES(:id, :storeno, :pdvno, :xano, :invno, :nfeno, :nfese,
              |:nfsno, :nfsse, :localizacao)
              |""".trimMargin()
  override val sqlUpdate: String
    get() = """UPDATE t_devolucao_fornecedor
              |SET nfeno = :nfeno,
              |    nfese = :nfese,
              |    nfsno = :nfsno,
              |    nfsse = :nfsse
              |WHERE id = :id
    """.trimMargin()
  
  companion object: ETLThread<DevolucaoFornecedor>(ETLDevolucaoFornecedor(), 60) {
    val sql
      get() = "select * FROM t_devolucao_fornecedor"
    
    override fun getSource() = saci.findDevolucaoFornecedor(RegistryUserInfo.lojaDeposito.numero)
    
    override fun getTarget() = DB.findDto(DevolucaoFornecedor::class.java,
                                          sql).findList()
  }
}