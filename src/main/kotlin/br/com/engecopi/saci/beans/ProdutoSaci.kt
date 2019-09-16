package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.utils.lpad

class ProdutoSaci(val prdno: String, val grade: String) {
  val prd: Produto? by lazy {
    produtos.firstOrNull { it.codigo == prdno.lpad(16, " ") && it.grade == grade}
  }
  val dataCadastro = prd?.dataCadastro

  companion object{
    val produtos = mutableListOf<Produto>()

    fun updateProduto(){
      produtos.clear()
      produtos.addAll(Produto.all())
    }
  }
}