package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import com.github.mvysny.karibudsl.v8.AutoView

class AbreciacaoViewModel(view: IView): ViewModel(view) {
  fun saveAbreviacao(bean: Abreviacao?) = exec {
    bean?.save()
  }

  val abreviacaoes get() = Abreviacao.findAll()

  init {
    Abreviacao.updateAbreviacao()
  }
}