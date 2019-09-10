package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RepositoryAvisoNotas
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import br.com.engecopi.saci.beans.NFEntrada
import br.com.engecopi.saci.beans.NFSaida

class PainelGeralViewModel(view: IView): ViewModel(view) {
  val repository = RepositoryAvisoNotas

  fun listSaidaCancelada(): List<NFSaida> {
    return repository.notaSaidaCancelada()
      .sortedBy {-it.date}
  }

  fun listEntradaCancelada(): List<NFEntrada> {
    return repository.notaEntradaCancelada()
      .sortedBy {-it.date}
  }

  fun listSaidaPendente(): List<NFSaida> {
    return repository.notaSaidaPendente()
      .sortedBy {-it.date}
  }

  fun listEntradaPendente(): List<NFEntrada> {
    return repository.notaEntradaPendente()
      .sortedBy {-it.date}
  }

  fun refresh() = exec {
    repository.refresh()
  }
}