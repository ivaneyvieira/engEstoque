package br.com.engecopi.estoque.model

import br.com.engecopi.saci.beans.DevolucaoFornecedor
import br.com.engecopi.saci.beans.NFEntrada
import br.com.engecopi.saci.beans.NFSaida
import br.com.engecopi.saci.saci

object RepositoryAvisoNotas {
  private val storeno = RegistryUserInfo.lojaDefault.numero
  private val abreviacao = RegistryUserInfo.abreviacaoDefault
  private val notaSaidaTodas = mutableListOf<NFSaida>()
  private val notaEntradaTodas = mutableListOf<NFEntrada>()
  private val devolucaoFornecedor = mutableListOf<DevolucaoFornecedor>()
  private val notaEntradaSalva = mutableListOf<Nota>()
  private val notaSaidaSalva = mutableListOf<Nota>()

  init {
    refresh()
  }

  fun refresh() {
    synchronized(this) {
      refreshDevolucaoFornecedor()
      refreshNotaSaidaTodas()
      refreshNotaEntradaTodas()
      refreshNotaEntradaApp()
      refreshNotaSaidaApp()
    }
  }

  private fun refreshNotaSaidaApp() {
    notaSaidaSalva.clear()
    notaSaidaSalva.addAll(Nota.notasSaidaSalva())
  }

  private fun refreshNotaEntradaApp() {
    notaEntradaSalva.clear()
    notaEntradaSalva.addAll(Nota.notasEntradaSalva())
  }

  private fun refreshDevolucaoFornecedor() {
    devolucaoFornecedor.clear()
    devolucaoFornecedor.addAll(saci.findDevolucaoFornecedor(storeno, abreviacao))
  }

  private fun refreshNotaEntradaTodas() {
    notaEntradaTodas.clear()
    notaEntradaTodas.addAll(saci.findNotaEntradaTodas(storeno, abreviacao))
  }

  private fun refreshNotaSaidaTodas() {
    notaSaidaTodas.clear()
    notaSaidaTodas.addAll(saci.findNotaSaidaTodas(storeno, abreviacao
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ))
  }

  fun notaEntradaDevolvida(): List<NFEntrada> {
    return notaEntradaTodas.filter {nfEntrada ->
      devolucaoFornecedor.any {dev ->
        dev.numeroSerieEntrada == nfEntrada.numero
      }
    }
  }

  fun notaEntradaCancelada(): List<NFEntrada> {
    return notaEntradaTodas.filter {nfEntrada ->
      notaEntradaSalva.any {nota ->
        nota.numero == nfEntrada.numeroSerie
      } && nfEntrada.boolCancelado
    }
  }

  fun notaSaidaCancelada(): List<NFSaida> {
    return notaSaidaTodas.filter {nfSaida ->
      notaSaidaSalva.any {nota ->
        nota.numero == nfSaida.numeroSerie
      } && nfSaida.boolCancelado
    }
  }

  fun notaEntradaPendente(): List<NFEntrada> {
    return notaEntradaTodas.filter {nfEntrada ->
      !notaEntradaSalva.any {nota ->
        nota.numero == nfEntrada.numeroSerie
      }
    }
  }

  fun notaSaidaPendente(): List<NFSaida> {
    return notaSaidaTodas.filter {nfSaida ->
      !notaSaidaSalva.any {nota ->
        nota.numero == nfSaida.numeroSerie
      }
    }
  }
}