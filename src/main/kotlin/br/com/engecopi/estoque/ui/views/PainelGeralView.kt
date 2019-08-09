package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.viewmodel.PainelGeralViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.expand
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.cssLayout
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isExpanded
import com.vaadin.ui.themes.ValoTheme

@AutoView("painel")
class PainelGeralView: LayoutView<PainelGeralViewModel>() {
  init {
    viewModel = PainelGeralViewModel(this)
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