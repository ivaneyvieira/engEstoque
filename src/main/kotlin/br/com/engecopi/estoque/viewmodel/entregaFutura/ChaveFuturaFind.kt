package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.*
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENotaNaoEntregaFutura
import br.com.engecopi.estoque.viewmodel.ENovaBaixaLancada
import br.com.engecopi.saci.beans.NotaProdutoSaci

class ChaveFuturaFind {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaKey = KeyNota(key)
    val storeno = notaKey.storeno
    val nfno = notaKey.numero
    val notaSaci = Nota.findNotaSaidaSaci(storeno, nfno)
    val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
    val numero = nota.numero ?: ""
    return when {
      nota.isNotaBaixaLancada() -> throw ENovaBaixaLancada()
      nota.tipoNota() != VENDAF -> throw ENotaNaoEntregaFutura(numero)
      else                      -> notaSaci
    }.expandeGradeGenerica()
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
    return this.flatMap { notaSaci ->
      val gradeStr = notaSaci.grade ?: ""
      if (gradeStr.startsWith("***")) {
        Produto.findProdutos(notaSaci.codigo()).mapNotNull { produto ->
          val quant = notaSaci.quant ?: 0 //if(produto.saldoTotal() >= quant) {
          notaSaci.copy(grade = produto.grade).apply {
            this.gradeGenerica = true
          } // }
          // else null
        }
      }
      else listOf(notaSaci.apply {
        this.gradeGenerica = false
      })
    }
  }

  private fun List<NotaProdutoSaci>.filtroTipoCompativel(): List<NotaProdutoSaci> {
    return this.filter { nota ->
      val tipo = nota.tipoNota() ?: return@filter false
      return@filter usuarioDefault.isTipoCompativel(tipo)
    }
  }
}


