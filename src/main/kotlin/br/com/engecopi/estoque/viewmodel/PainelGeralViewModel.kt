package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RepositoryAvisoNotas
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import br.com.engecopi.saci.beans.NotaSaci

class PainelGeralViewModel(view: IPainelGeralView): ViewModel<IPainelGeralView>(view) {
  init {
    RepositoryAvisoNotas.refresh()
  }

  fun listSaidaCancelada(): List<NotaSaci> {
    return RepositoryAvisoNotas.notaSaidaCancelada()
      .sortedBy {-it.date}
  }

  fun listEntradaCancelada(): List<NotaSaci> {
    return RepositoryAvisoNotas.notaEntradaCancelada()
      .sortedBy {-it.date}
  }

  fun listSaidaPendente(): List<NotaSaci> {
    return RepositoryAvisoNotas.notaSaidaPendente()
      .sortedBy {-it.date}
  }

  fun listEntradaPendente(): List<NotaSaci> {
    return RepositoryAvisoNotas.notaEntradaPendente()
      .sortedBy {-it.date}
  }

  fun refresh() = exec {
    RepositoryAvisoNotas.refresh()
    view.updateView()
  }
}

interface IPainelGeralView : IView{
  fun updateView()
}