package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName

@TableName("t_dados_produto_saci")
class DadosProdutosSaci(id: String,
                        val storeno: Int,
                        val codigo: String,
                        val grade: String,
                        val nome: String,
                        val unidade: String,
                        val comp: Double,
                        val larg: Double,
                        val alt: Double,
                        val localizacao: String,
                        val abreviacao: String): EntryID(id) {
  override val chave: String
    get() = "$nome;$unidade;$comp;$larg;$alt"
}