package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewProdutoLoc.Find.filtraLoc
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENotaEntregaFutura
import br.com.engecopi.estoque.viewmodel.ENovaFaturaLancada
import br.com.engecopi.saci.beans.NotaProdutoSaci

class NFExpedicaoFind() {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaSaci = when(key.length) {
      44   -> Nota.findNotaSaidaKey(key)
      else -> {
        val keyNota = KeyNota(key)
        Nota.findNotaSaidaSaci(keyNota.storeno, keyNota.numero)
      }
    }.filtroLocalizacao()
    val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
    val numero = nota.numero ?: ""
    return when {
      nota.isNotaFaturaLancada()        -> throw ENovaFaturaLancada()
      nota.tipoNota() == VENDAF         -> throw ENotaEntregaFutura(numero)
      usuarioDefault.isEstoqueExpedicao -> notaSaci.filtroTipoCompativel()
      else                              -> emptyList()
    }
  }
  
  private fun List<NotaProdutoSaci>.filtroTipoCompativel(): List<NotaProdutoSaci> {
    return this.filter {nota ->
      val tipo = nota.tipoNota() ?: return@filter false
      return@filter usuarioDefault.isTipoCompativel(tipo)
    }
  }
  
  private fun List<NotaProdutoSaci>.filtroLocalizacao(): List<NotaProdutoSaci> {
    return this.filter {ns ->
      when {
        usuarioDefault.isEstoqueExpedicao -> filtraLoc(ns.prdno, ns.grade)
        else                              -> usuarioDefault.admin
      }
    }
  }
}