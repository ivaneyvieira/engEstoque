package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName
import br.com.engecopi.utils.lpad

@TableName("t_produto_saci")
class TabProdutoSaci(
  id: String,
  val storeno: Int,
  var codigo: String,
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
    get() = "$codigo$nome$custo$unidade$tipo$comp$larg$alt"
  
  fun updateProduto(): TabProdutoSaci {
    if(codigo.length < 16)
      codigo = codigo.lpad(16, " ")
    return this
  }
}
