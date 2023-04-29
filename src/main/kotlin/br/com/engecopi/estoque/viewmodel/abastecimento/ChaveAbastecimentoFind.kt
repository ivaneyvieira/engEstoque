package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_A
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENaoAbastecimento
import br.com.engecopi.saci.beans.NotaProdutoSaci

class ChaveAbastecimentoFind() {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaSaci = Nota.findNotaSaidaSaci(lojaDeposito, key).ifEmpty {
      val keyNota = KeyNota(key)
      Nota.findNotaSaidaSaci(keyNota.storeno, keyNota.numero)
    }
    val nota = notaSaci.firstOrNull() ?: throw EChaveNaoEncontrada()
    val numero = nota.numero ?: ""
    return when {
      nota.tipoNota() != PEDIDO_A -> throw ENaoAbastecimento(numero)
      else -> notaSaci
    }
  }
}