package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENotaNaoEntregaFutura
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.beans.NotaProdutoSaci

class NotaFuturaFind {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaKey = KeyNota(key)
    val storeno = notaKey.storeno
    val nfno = notaKey.numero
    val notaSaci =
      Nota.findNotaSaidaSaci(storeno, nfno)
        .filter {ns ->
          when {
            RegistryUserInfo.usuarioDefault.isEstoqueVendaFutura -> ViewProdutoLoc.filtraLoc(ns.prdno, ns.grade)
            else                                                 -> true
          }
        }
    val numero = notaSaci.firstOrNull()?.numero ?: ""
    return when {
      notaSaci.isEmpty()                           -> throw EChaveNaoEncontrada()
      notaSaci.firstOrNull()?.tipoNota() != VENDAF -> throw ENotaNaoEntregaFutura(numero)
      else                                         -> if(RegistryUserInfo.usuarioDefault.isEstoqueVendaFutura) {
        val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
        val notaSerie = nota.notaSerie() ?: throw EChaveNaoEncontrada()
        val tipo = notaSerie.tipoNota
        when {
          RegistryUserInfo.usuarioDefault.isTipoCompativel(tipo) -> notaSaci
          else                                                   -> throw EViewModelError("O usuário não está habilitado para lançar esse tipo de nota (${notaSerie.descricao})")
        }
      }
      else notaSaci
    }
  }
  
  private fun NotaProdutoSaci.notaSerie(): NotaSerie? {
    val tipo = TipoNota.value(tipo)
    return NotaSerie.findByTipo(tipo)
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
}