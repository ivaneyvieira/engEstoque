package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

class DlgCodigoBarras(
  textField: TextField, private val confirmaClose: Boolean, processaleitura: (Nota?, String) -> ItemNota?
) : Window(titulo) {
  private var nota: Nota? = null

  init {
    center()
    isClosable = false
    w = 400.px
    isModal = true
    isResizable = false
    content = VerticalLayout().apply {
      isMargin = true
      isSpacing = true
      textField.w = 100.perc
      addComponent(textField)
      horizontalLayout {
        alignment = Alignment.BOTTOM_RIGHT
        button("Fecha") {
          setClickShortcut(KeyCode.ESCAPE)
          alignment = Alignment.BOTTOM_RIGHT
          addClickListener {
            close()
          }
        }
        button("Confirma") {
          addStyleName(ValoTheme.BUTTON_PRIMARY)
          setClickShortcut(KeyCode.ENTER)
          alignment = Alignment.BOTTOM_RIGHT
          addClickListener {
            val item = processaleitura(nota, textField.value)
            if (item == null) {
              textField.selectAll()
            } else {
              textField.value = ""
              nota = item.nota
            }
            if (confirmaClose) close()
          }
        }
      }
    }

    addFocusListener {
      textField.focus()
    }
  }

  companion object {
    const val titulo = "Leitura"
  }
}

fun readString(msg: String, confirmaClose: Boolean, processaleitura: (Nota?, String) -> ItemNota?) {
  if (msg.isNotBlank()) {
    val textField = TextField(msg).apply {
      this.w = 400.px
    }
    val dlg = DlgCodigoBarras(textField, confirmaClose, processaleitura)
    UI.getCurrent().addWindow(dlg)
    textField.focus()
  }
}
