package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.viewmodel.AbreciacaoViewModel
import br.com.engecopi.estoque.viewmodel.IAbreciacaoView
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.title
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.grid
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.ui.CheckBox
import com.vaadin.ui.TextField
import com.vaadin.ui.renderers.TextRenderer
import org.vaadin.patrik.FastNavigation

@AutoView
class AbreciacaoView: LayoutView<AbreciacaoViewModel>(), IAbreciacaoView {

  init {
    viewModel = AbreciacaoViewModel(this)

    title("Localizações")

    grid(Abreviacao::class) {
      val edtLoc = CheckBox()
      val edtImpressora = TextField()

      expand()
      dataProvider = ListDataProvider(viewModel.abreviacaoes)
      removeAllColumns()
      addColumnFor(Abreviacao::abreviacao) {
        caption = "Localização"
      }

      addColumnFor(Abreviacao::expedicao) {
        caption = "Expedição"
        setRenderer({value -> if(value) "Sim" else "Não"}, TextRenderer())
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
      editor.saveCaption = "Salvar"
      editor.isBuffered = false
      nav.addEditorCloseListener {
        viewModel.saveAbreviacao()
      }
    }
  }
}