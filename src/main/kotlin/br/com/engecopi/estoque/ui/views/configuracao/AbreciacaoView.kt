package br.com.engecopi.estoque.ui.views.configuracao

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.viewmodel.configuracao.AbreciacaoViewModel
import br.com.engecopi.estoque.viewmodel.configuracao.IAbreciacaoView
import br.com.engecopi.framework.model.AppPrinter
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.title
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.CheckBox
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.renderers.TextRenderer
import org.vaadin.patrik.FastNavigation

@AutoView
class AbreciacaoView : LayoutView<AbreciacaoViewModel>(), IAbreciacaoView {

  private var gridAbrev: Grid<Abreviacao>? = null

  init {
    viewModel = AbreciacaoViewModel(this)

    title("Localizações")

    horizontalLayout {
      button("Remover") {
        icon = VaadinIcons.TRASH
        onLeftClick {
          val itens = gridAbrev?.selectedItems.orEmpty().toList()
          viewModel.removeAbreviacao(itens)
        }
      }
      button("Adicionar") {
        icon = VaadinIcons.PLUS
        onLeftClick {
          viewModel.addAbreviacao()
        }
      }
    }

    gridAbrev = grid(Abreviacao::class) {
      setSelectionMode(Grid.SelectionMode.MULTI)
      val edtAbreviacao = TextField()
      val edtLoc = CheckBox()
      val edtImpressora = ComboBox<String>().apply {
        val itens = AppPrinter.appPrinterNames
        setItems(itens)
        isTextInputAllowed = false
      }

      expand()
      dataProvider = ListDataProvider(viewModel.abreviacaoes)
      removeAllColumns()

      addColumnFor(Abreviacao::abreviacao) {
        caption = "Localização"
        setEditorComponent(edtAbreviacao)
      }

      addColumnFor(Abreviacao::expedicao) {
        caption = "Expedição"
        setRenderer({ value -> if (value) "Sim" else "Não" }, TextRenderer())
        setEditorComponent(edtLoc)
      }

      addColumnFor(Abreviacao::impressora) {
        caption = "Impressora"
        setEditorComponent(edtImpressora)
      }

      editor.isEnabled = true
      val nav = FastNavigation(this, false, true)
      nav.changeColumnAfterLastRow = true
      nav.openEditorWithSingleClick = true
      nav.allowArrowToChangeRow = true
      nav.openEditorOnTyping = true
      nav.addEditorSaveShortcut(KeyCode.ENTER)
      editor.cancelCaption = "Cancelar"
      editor.saveCaption = "Salvar" //editor.isBuffered = false
      nav.addEditorCloseListener {
        viewModel.saveAbreviacao()
      }
    }
  }

  override fun updateGrid() {
    gridAbrev?.dataProvider = ListDataProvider(viewModel.abreviacaoes)
  }
}

