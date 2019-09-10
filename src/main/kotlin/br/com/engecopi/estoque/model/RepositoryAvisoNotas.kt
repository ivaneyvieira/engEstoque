package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.TipoNota.COMPRA
import br.com.engecopi.estoque.model.TipoNota.DEV_CLI
import br.com.engecopi.estoque.model.TipoNota.DEV_FOR
import br.com.engecopi.saci.beans.DevolucaoFornecedor
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.saci.saci

class RepositoryAvisoNotas {
  private val storeno = RegistryUserInfo.lojaDefault.numero
  private val abreviacao = RegistryUserInfo.abreviacaoDefault
  private val notaSaidaTodas = mutableListOf<NotaSaci>()
  private val notaEntradaTodas = mutableListOf<NotaSaci>()
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
    notaEntradaTodas.addAll(saci.findNotaEntradaSaci(storeno, abreviacao))
  }

  private fun refreshNotaSaidaTodas() {
    notaSaidaTodas.clear()
    notaSaidaTodas.addAll(saci.findNotaSaidaSaci(storeno, abreviacao))
  }

  fun notaEntradaDevolvida(): List<NotaSaci> {
    return notaEntradaTodas.filter {nfEntrada ->
      devolucaoFornecedor.any {dev ->
        dev.numeroSerieEntrada == nfEntrada.numero
      }
    }
  }

  fun notaEntradaCancelada(): List<NotaSaci> {
    return notaEntradaTodas.filter {nfEntrada ->
      notaEntradaSalva.any {nota ->
        nota.numero == nfEntrada.numeroSerie
      } && nfEntrada.boolCancelado
    }
  }

  fun notaSaidaCancelada(): List<NotaSaci> {
    return notaSaidaTodas.filter {nfSaida ->
      notaSaidaSalva.any {nota ->
        nota.numero == nfSaida.numeroSerie
      } && nfSaida.boolCancelado
    }
  }

  fun notaEntradaPendente(): List<NotaSaci> {
    return notaEntradaTodas
      .filter {nfEntrada ->
        nfEntrada.entradaAceita() && !nfEntrada.boolCancelado
      }
      .filter {nfEntrada ->
        !notaEntradaSalva.any {nota ->
          nota.numero == nfEntrada.numeroSerie
        }
      }
  }

  fun notaSaidaPendente(): List<NotaSaci> {
    return notaSaidaTodas
      .filter {nfSaida ->
        nfSaida.saidaAceita() && !nfSaida.boolCancelado
      }
      .filter {nfSaida ->
        !notaSaidaSalva.any {nota ->
          nota.numero == nfSaida.numeroSerie
        }
      }
  }

  private fun NotaSaci.entradaAceita() = tipoNota == DEV_CLI || tipoNota == COMPRA
  private fun NotaSaci.saidaAceita(): Boolean = tipoNota == DEV_FOR
}


