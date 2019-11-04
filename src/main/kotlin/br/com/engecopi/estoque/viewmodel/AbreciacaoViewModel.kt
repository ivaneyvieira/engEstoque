package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import com.github.mvysny.karibudsl.v8.AutoView

class AbreciacaoViewModel(view: IAbreciacaoView): ViewModel<IAbreciacaoView>(view) {
  fun saveAbreviacao() = exec {
    abreviacaoes.forEach {it.save()}
  }

  val abreviacaoes = mutableListOf<Abreviacao>()

  init {
    Abreviacao.updateAbreviacao()
    updateAbreviacao()
  }

  fun updateAbreviacao() {
    abreviacaoes.clear()
    abreviacaoes.addAll(Abreviacao.findAll())
  }
}

interface IAbreciacaoView : IView{

}