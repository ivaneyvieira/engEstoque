package br.com.engecopi.estoque.ui.views.paineis

import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.paineis.IPainelGeralView
import br.com.engecopi.estoque.viewmodel.paineis.PainelGeralViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.saci.beans.NotaSaci
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.CssLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

@AutoView("painel")
class PainelGeralView : LayoutView<PainelGeralViewModel>(), IPainelGeralView {
  val saidaPendenteDataProvider = ListDataProvider<NotaSaci>(mutableListOf())
  val entradaPendenteDataProvider = ListDataProvider<NotaSaci>(mutableListOf())
  val saidaCanceladaDataProvider = ListDataProvider<NotaSaci>(mutableListOf())
  val entradaCanceladaDataProvider = ListDataProvider<NotaSaci>(mutableListOf())

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
        this.gridNotaSaci(saidaPendenteDataProvider, SAIDA)
      }
      cssLayout("Nota de entrada pendente") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridNotaSaci(entradaPendenteDataProvider, ENTRADA)
      }
    }
    horizontalLayout {
      isExpanded = true
      setSizeFull()
      cssLayout("Notas de saídas cancelada") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridNotaSaci(saidaCanceladaDataProvider, SAIDA)
      }
      cssLayout("Nota de entrada cancelada") {
        expand()
        addStyleName(ValoTheme.LAYOUT_CARD)
        this.gridNotaSaci(entradaCanceladaDataProvider, ENTRADA)
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

  private fun CssLayout.gridNotaSaci(dataProvider: ListDataProvider<NotaSaci>, tipoMov: TipoMov): Grid<NotaSaci> {
    return grid(dataProvider = dataProvider) {
      setSizeFull()
      if (tipoMov == ENTRADA) {
        addColumnFor(NotaSaci::invno) {
          caption = "NI"
        }
      }
      addColumnFor(NotaSaci::numeroSerie) {
        caption = "Número"
      }
      addColumnFor(NotaSaci::localDate) {
        caption = "Data"
        dateFormat()
      }
      addColumnFor(NotaSaci::tipo) {
        caption = "Tipo"
        setRenderer({ tipo -> TipoNota.value(tipo)?.descricao ?: "" }, TextRenderer())
      }
    }
  }
}


