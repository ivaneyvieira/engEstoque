package br.com.engecopi.estoque.model

import br.com.engecopi.utils.lpad
import io.ebean.DB
import java.time.LocalDate

val SQL_VALIDADE = """
  SELECT I.produto_id,
         I.localizacao,
         I.data_validade                AS dataValidade,
         SUM((CASE I.status
               WHEN 'RECEBIDO'  THEN 1.00
               WHEN 'CONFERIDA' THEN -1.00
               WHEN 'ENTREGUE'  THEN -1.00
               ELSE 0.00
             END) * I.quantidade)       AS saldoVencimento
  FROM engEstoque.itens_nota AS    I
    INNER JOIN engEstoque.produtos P
                 ON I.produto_id = P.id
  WHERE P.codigo = :codigo
  GROUP BY I.produto_id, I.localizacao, I.data_validade
  HAVING saldoVencimento > 0.00
  ORDER BY IFNULL(data_validade, DATE(20100101))
""".trimIndent()

fun findProdutoValidade(codigo: String): List<ProdutoValidade> {
  val codigoProduto = if (codigo.length < 16) codigo.lpad(16, " ") else codigo
  return DB.findDto(ProdutoValidade::class.java, SQL_VALIDADE).setParameter("codigo", codigoProduto).findList()
}

fun fisrtProdutoValidade(codigo: String): ProdutoValidade? {
  val produtoValidadeList = findProdutoValidade(codigo).sortedBy { it.dataValidade ?: LocalDate.MIN }
  return produtoValidadeList.firstOrNull()
}

data class ProdutoValidade(
  val produto_id: Long,
  val localizacao: String,
  val dataValidade: LocalDate?,
  val saldoVencimento: Double,
)