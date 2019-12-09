package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.estoque.viewmodel.ENotaEntregaFutura
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.beans.NotaProdutoSaci

class NFExpedicaoFind {
  fun findNotaSaidaKey(key: String): List<NotaProdutoSaci> {
    val notaSaci = when(key.length) {
      44   -> Nota.findNotaSaidaKey(key)
      else -> Nota.findNotaSaidaSaci(RegistryUserInfo.lojaDeposito, key)
    }.filter {ns ->
      when {
        usuarioDefault.isEstoqueExpedicao -> ViewProdutoLoc.filtraLoc(ns.prdno, ns.grade)
        else                              -> true
      }
    }
    val numero = notaSaci.firstOrNull()?.numero ?: ""
    return when {
      notaSaci.isEmpty()                           -> throw EChaveNaoEncontrada()
      notaSaci.firstOrNull()?.tipoNota() == VENDAF -> throw ENotaEntregaFutura(numero)
      else                                         -> if(RegistryUserInfo.usuarioDefault.isEstoqueExpedicao) {
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
  
  fun NotaProdutoSaci.notaSerie(): NotaSerie? {
    val tipo = TipoNota.value(tipo)
    return NotaSerie.findByTipo(tipo)
  }
}