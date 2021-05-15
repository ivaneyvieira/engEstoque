package br.com.engecopi.estoque.ui.views

import com.github.mvysny.karibudsl.v8.*
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.ui.Alignment
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.themes.ValoTheme

class PnlCodigoBarras(caption: String, processaleitura: (String) -> Unit) : HorizontalLayout() {
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
