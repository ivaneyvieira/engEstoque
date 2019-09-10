package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.PainelGeralViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.saci.beans.NFEntrada
import br.com.engecopi.saci.beans.NFSaida
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.column
import com.github.mvysny.karibudsl.v8.cssLayout
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isExpanded
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.CssLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

@AutoView("painel")
class PainelGeralView: LayoutView<PainelGeralViewModel>() {
  val saidaPendenteDataProvider = ListDataProvider<NFSaida>(mutableListOf())
  val entradaPendenteDataProvider = ListDataProvider<NFEntrada>(mutableListOf())
  val saidaCanceladaDataProvider = ListDataProvider<NFSaida>(mutableListOf())
  val entradaCanceladaDataProvider = ListDataProvider<NFEntrada>(mutableListOf())

  init {
    viewModel = PainelGeralViewModel(this)
    setSizeFull()
    form("Painel de notas canceladas e pendente")
    horizontalLayout {
      cssLayout {
        button {
          icon = VaadinIcons.REFRESH
          addClickListener {
            viewModel.refresh()
          }
        }
      }
    }
    horizontalLayout {
      expand()
      cssLayout("Notas de saídas pendente") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridSaida(saidaPendenteDataProvider)
      }
      cssLayout("Nota de entrada pendente") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridEntrada(entradaPendenteDataProvider)
      }
    }
    horizontalLayout {
      isExpanded = true
      setSizeFull()
      cssLayout("Notas de saídas cancelada") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridSaida(saidaCanceladaDataProvider)
      }
      cssLayout("Nota de entrada cancelada") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridEntrada(entradaCanceladaDataProvider)
      }
    }
    updateView()
  }

  override fun updateView() {
    saidaPendenteDataProvider.items.clear()
    saidaPendenteDataProvider.items.addAll(viewModel.listSaidaPendente())

    entradaPendenteDataProvider.items.clear()
    entradaPendenteDataProvider.items.addAll(viewModel.listEntradaPendente())

    saidaCanceladaDataProvider.items.clear()
    saidaCanceladaDataProvider.items.addAll(viewModel.listSaidaCancelada())

    entradaCanceladaDataProvider.items.clear()
    entradaCanceladaDataProvider.items.addAll(viewModel.listEntradaCancelada())
  }

  override fun updateModel() {
    //Vazio
  }

  private fun CssLayout.gridSaida(saidaDataProvider: ListDataProvider<NFSaida>): Grid<NFSaida> {
    return grid(dataProvider = saidaDataProvider) {
      setSizeFull()
      addColumnFor(NFSaida::numeroSerie) {
        caption = "Número"
      }
      addColumnFor(NFSaida::localDate) {
        caption = "Data"
        dateFormat()
      }
      addColumnFor(NFSaida::tipo) {
        caption = "Tipo"
        setRenderer({tipo -> TipoNota.value(tipo)?.descricao ?: ""}, TextRenderer())
      }
    }
  }

  private fun CssLayout.gridEntrada(entradaDataProvider: ListDataProvider<NFEntrada>): Grid<NFEntrada> {
    return grid(dataProvider = entradaDataProvider) {
      setSizeFull()
      addColumnFor(NFEntrada::numeroSerie) {
        caption = "Número"
      }
      addColumnFor(NFEntrada::localDate) {
        caption = "Data"
        dateFormat()
      }
      addColumnFor(NFEntrada::tipo) {
        caption = "Tipo"
        setRenderer({tipo -> TipoNota.value(tipo)?.descricao ?: ""}, TextRenderer())
      }
    }
  }
}


