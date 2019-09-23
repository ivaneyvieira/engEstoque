package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RepositoryAvisoNotas
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import br.com.engecopi.saci.beans.NotaSaci

class PainelGeralViewModel(view: IView): ViewModel(view) {
  val repository = RepositoryAvisoNotas().apply {
    refresh()
  }

  fun listSaidaCancelada(): List<NotaSaci> {
    return repository.notaSaidaCancelada()
      .sortedBy {-it.date}
  }

  fun listEntradaCancelada(): List<NotaSaci> {
    return repository.notaEntradaCancelada()
      .sortedBy {-it.date}
  }

  fun listSaidaPendente(): List<NotaSaci> {
    return repository.notaSaidaPendente()
      .sortedBy {-it.date}
  }

  fun listEntradaPendente(): List<NotaSaci> {
    return repository.notaEntradaPendente()
      .sortedBy {-it.date}
  }

  fun refresh() = exec {
    repository.refresh()
  }
}