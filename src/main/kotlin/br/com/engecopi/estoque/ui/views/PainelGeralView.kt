package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.viewmodel.PainelGeralModel
import br.com.engecopi.framework.ui.view.LayoutView
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.cssLayout
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.label
import com.github.mvysny.karibudsl.v8.perc
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.ui.themes.ValoTheme

@AutoView("painel")
class PainelGeralView: LayoutView<PainelGeralModel>() {

  init {
    w = 100.perc
    horizontalLayout {
      w = 50.perc
      cssLayout {
        expandRatio = 1f
        addStyleName(ValoTheme.LAYOUT_CARD)
        label("1")
      }
      cssLayout {
        expandRatio = 1f
        addStyleName(ValoTheme.LAYOUT_CARD)
        label("2")
      }
    }
    horizontalLayout {
      w = 50.perc
      cssLayout {
        expandRatio = 1f
        addStyleName(ValoTheme.LAYOUT_CARD)
        label("3")
      }
    }
  }

  override fun updateView() {
  }

  override fun updateModel() {
  }
}