package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.ItemNota
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.isMargin
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.ui.Alignment
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.themes.ValoTheme

class PnlCodigoBarras(caption: String, processaleitura: (String) -> Unit): HorizontalLayout() {
  private val textField: TextField = textField(caption) {
    w = 400.px
    focus()
  }

  init {
    isMargin = false
    isSpacing = true

    button("Confirma") {
      addStyleName(ValoTheme.BUTTON_PRIMARY)
      setClickShortcut(KeyCode.ENTER)
      alignment = Alignment.BOTTOM_RIGHT
      addClickListener {
        processaleitura(textField.value)
        focusEdit()
        textField.selectAll()
      }
    }
  }

  fun focusEdit() {
    textField.value = ""
    textField.focus()
  }
}
