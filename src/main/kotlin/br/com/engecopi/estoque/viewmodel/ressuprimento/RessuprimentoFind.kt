package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.saci.saci

class RessuprimentoFind(val view: IPedidoRessuprimentoView) {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    return saci.findPedidoRessuprimento(key.toIntOrNull())
  }
  
  fun findLoja(storeno: Int?): Loja? = Loja.findLoja(storeno)
  
  fun abreviacoes(prdno: String?, grade: String?): List<String> {
    val produto = Produto.findProduto(prdno, grade) ?: return emptyList()
    return ViewProdutoLoc.abreviacoesProduto(produto)
  }
  
  fun saldoProduto(notaProdutoSaci: NotaProdutoSaci, abreviacao: String): Int {
    val produto = Produto.findProduto(notaProdutoSaci.codigo(), notaProdutoSaci.grade)
    return produto?.saldoAbreviacao(abreviacao) ?: 0
  }
  
  private fun List<NotaProdutoSaci>.expandeGradeGenerica(): List<NotaProdutoSaci> {
    return this.flatMap {notaSaci ->
      val gradeStr = notaSaci.grade ?: ""
      if(gradeStr.startsWith("***")) {
        Produto.findProdutos(notaSaci.codigo())
          .map {produto ->
            val quant = notaSaci.quant ?: 0
            notaSaci.copy(grade = produto.grade)
              .apply {
                this.gradeGenerica = true
              }
          }
      }
      else listOf(notaSaci.apply {
        this.gradeGenerica = false
      })
    }
  }
}


