package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.viewmodel.PainelGeralModel
import br.com.engecopi.estoque.viewmodel.UsuarioViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.expand
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.cssLayout
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isExpanded
import com.github.mvysny.karibudsl.v8.label
import com.github.mvysny.karibudsl.v8.perc
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.ui.themes.ValoTheme

@AutoView("painel")
class PainelGeralView: LayoutView<PainelGeralModel>() {


  init {
    viewModel = PainelGeralModel(this)
    setSizeFull()
    horizontalLayout {
      expand()
      cssLayout("Notas de saídas") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
      }
      cssLayout("Nota de entrada") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
      }
    }
    horizontalLayout {
      isExpanded = true
      setSizeFull()
      cssLayout("Produtos com saldo negativo") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
      }
    }
    updateView()
  }

  override fun updateView() {
    val listSaidaCancel = viewModel.listSaidaCancel()
  }

  override fun updateModel() {
  }
}