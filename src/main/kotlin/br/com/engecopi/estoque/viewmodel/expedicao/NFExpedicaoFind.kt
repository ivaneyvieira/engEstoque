package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENotaEntregaFutura
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.beans.NotaProdutoSaci

class NFExpedicaoFind() {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaSaci = when(key.length) {
      44   -> Nota.findNotaSaidaKey(key)
      else -> {
        val keyNota = KeyNota(key)
        Nota.findNotaSaidaSaci(keyNota.storeno, keyNota.numero)
      }
    }.filtroNotaSaci()
    val numero = notaSaci.firstOrNull()?.numero ?: ""
    return when {
      notaSaci.isEmpty()                           -> throw EChaveNaoEncontrada()
      notaSaci.firstOrNull()?.tipoNota() == VENDAF -> throw ENotaEntregaFutura(numero)
      usuarioDefault.isEstoqueExpedicao            -> findNotaSaci(notaSaci)
      else                                         -> notaSaci
    }
  }
  
  private fun findNotaSaci(notaSaci: List<NotaProdutoSaci>): List<NotaProdutoSaci> {
    val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
    val notaSerie = nota.notaSerie() ?: throw EChaveNaoEncontrada()
    val tipo = notaSerie.tipoNota
    return when {
      usuarioDefault.isTipoCompativel(tipo) -> notaSaci
      else                                  -> throw EViewModelError("O usuário não está habilitado para lançar esse tipo de nota (${notaSerie.descricao})")
    }
  }
  
  private fun NotaProdutoSaci.notaSerie(): NotaSerie? {
    val tipo = TipoNota.value(tipo)
    return NotaSerie.findByTipo(tipo)
  }
  
  private fun List<NotaProdutoSaci>.filtroNotaSaci(): List<NotaProdutoSaci> {
    return this.filter {ns ->
      when {
        usuarioDefault.isEstoqueExpedicao -> ViewProdutoLoc.filtraLoc(ns.prdno, ns.grade)
        else                              -> true
      }
    }
  }
}