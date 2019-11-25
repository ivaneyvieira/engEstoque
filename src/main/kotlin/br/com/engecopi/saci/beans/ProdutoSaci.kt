package br.com.engecopi.saci.beans

import br.com.astrosoft.utils.lpad
import br.com.engecopi.estoque.model.Produto

class ProdutoSaci(val prdno: String, val grade: String) {
  val prd: Produto? = produtos.firstOrNull {it.codigo == prdno.lpad(16, " ") && it.grade == grade}
  val dataCadastro = prd?.dataCadastro
  
  companion object {
    private val produtos = mutableListOf<Produto>()
    
    fun updateProduto() {
      produtos.clear()
      produtos.addAll(Produto.all())
    }
  }
}