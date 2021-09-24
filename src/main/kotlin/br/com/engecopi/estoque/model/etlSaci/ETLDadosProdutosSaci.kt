package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.DadosProdutosSaci
import br.com.engecopi.saci.saci
import io.ebean.DB

class ETLDadosProdutosSaci : ETL<DadosProdutosSaci>() {
  override val sqlDelete: String
    get() = "DELETE FROM t_dados_produto_saci WHERE id = :id"
  override val sqlInsert = """
      INSERT IGNORE INTO t_dados_produto_saci(id, storeno, codigo, grade, nome, unidade, comp, larg, alt, 
      localizacao, abreviacao, mesesValidade)
      VALUES(:id, :storeno, :codigo, :grade, :nome, :unidade, :comp, :larg, :alt, :localizacao, :abreviacao, 
      :mesesValidade)
    """.trimIndent()
  override val sqlUpdate = """
      UPDATE t_dados_produto_saci
      SET nome        = :nome,
          unidade     = :unidade,
          comp        = :comp,
          larg        = :larg,
          alt         = :alt,
          mesesValidade = :mesesValidade
      WHERE id = :id
    """.trimIndent()

  companion object : ETLThread<DadosProdutosSaci>(ETLDadosProdutosSaci(), 60) {
    val sql
      get() = "select * FROM t_dados_produto_saci"

    override fun getSource() = saci.findDadosProdutosSaci()

    override fun getTarget() = DB.findDto(DadosProdutosSaci::class.java, sql).findList()
  }
}
