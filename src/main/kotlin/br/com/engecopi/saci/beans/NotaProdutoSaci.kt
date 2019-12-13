package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.dtos.ProdutoGrade
import br.com.engecopi.utils.lpad

data class NotaProdutoSaci(val rota: String?,
                      val storeno: Int?,
                      val numero: String?,
                      val serie: String?,
                      val date: Int?,
                      val dtEmissao: Int?,
                      val prdno: String?,
                      val grade: String?,
                      val quant: Int?,
                      val vendName: String? = "",
                      val clienteName: String? = "",
                      val tipo: String?,
                      val invno: Int?) {
  fun isSave(): Boolean {
    return ItemNota.isSave(this)
  }
  
  fun codigo(): String {
    return prdno?.lpad(16, " ") ?: ""
  }
  
  fun numeroSerie(): String {
    numero ?: return ""
    return if(serie.isNullOrBlank()) numero
    else "$numero/$serie"
  }
  
  fun localizacaoes(): List<ViewProdutoLoc> {
    return ViewProdutoLoc.findByCodigoGrade(prdno, grade)
  }
  
  fun tipoNota(): TipoNota? = TipoNota.value(tipo)
  
  fun loja(): Loja? = Loja.findLoja(storeno)
  
  val chaveProdutoGrade: ProdutoGrade
    get() = ProdutoGrade(prdno = prdno?.trim() ?: "", grade = grade ?: "")
  val nome
    get() = Produto.findProduto(prdno, grade)?.descricao ?: ""
}