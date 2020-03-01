package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName

@TableName("t_produto_saci")
class TabProdutoSaci(
  id: String,
  val storeno: Int,
  val codigo: String,
  val nome: String,
  val grade: String,
  val localizacao: String,
  val abreviacao: String,
  val custo: Double,
  val unidade: String,
  val tipo: String,
  val comp: Double,
  val larg: Double,
  val alt: Double
                    ): EntryID(id) {
  override val chave: String
    get() = "$nome$custo$unidade$tipo$comp$larg$alt"
}
