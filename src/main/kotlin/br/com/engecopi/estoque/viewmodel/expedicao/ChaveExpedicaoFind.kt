package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota.*
import br.com.engecopi.estoque.viewmodel.*
import br.com.engecopi.saci.beans.NotaProdutoSaci

class ChaveExpedicaoFind {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaSaci = when (key.length) {
      44   -> Nota.findNotaSaidaKey(key)
      else -> {
        val keyNota = KeyNota(key)
        Nota.findNotaSaidaSaci(keyNota.storeno, keyNota.numero)
      }
    }
    val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
    val numero = nota.numero ?: ""
    return when {
      nota.isNotaFaturaLancada()  -> throw ENovaFaturaLancada()
      nota.tipoNota() == VENDAF   -> throw ENotaEntregaFutura(numero)
      nota.tipoNota() == PEDIDO_R -> throw EPedidoRessuprimento(numero)
      nota.tipoNota() == PEDIDO_A -> throw EPedidoAbastecimento(numero)
      else                        -> notaSaci.filtroTipoCompativel()
    }
  }

  private fun List<NotaProdutoSaci>.filtroTipoCompativel(): List<NotaProdutoSaci> {
    return this.filter { nota ->
      val tipo = nota.tipoNota() ?: return@filter false
      return@filter usuarioDefault.isTipoCompativel(tipo)
    }
  }
}