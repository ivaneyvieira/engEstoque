package br.com.engecopi.estoque.viewmodel.configuracao

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel

class AbreciacaoViewModel(view: IAbreciacaoView) : ViewModel<IAbreciacaoView>(view) {
  fun saveAbreviacao() = exec {
    abreviacaoes.forEach { it.save() }
  }

  val abreviacaoes = mutableListOf<Abreviacao>()

  init { //Abreviacao.updateAbreviacao(lojaDeposito)
    updateAbreviacao()
  }

  fun updateAbreviacao() {
    abreviacaoes.clear()
    abreviacaoes.addAll(Abreviacao.findAll())
  }

  fun addAbreviacao() {
    val loja = RegistryUserInfo.lojaDeposito
    val abreviacao = Abreviacao(loja = loja, abreviacao = "", expedicao = false, impressora = "")
    abreviacaoes.add(0, abreviacao)
    view.updateGrid()
  }

  fun removeAbreviacao(itens: List<Abreviacao>) {
    itens.forEach { abreviacao ->
      Abreviacao.deleteById(abreviacao.id)
      updateAbreviacao()
      view.updateGrid()
    }
  }
}

interface IAbreciacaoView : IView {
  fun updateGrid()
}