package br.com.engecopi.framework.ui.view

import br.com.engecopi.utils.makeResource
import com.vaadin.server.Page
import com.vaadin.ui.Component
import com.vaadin.ui.Embedded
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import de.steinwedel.messagebox.ButtonOption
import de.steinwedel.messagebox.MessageBox

object MessageDialog {
  val ui = UI.getCurrent()!!
  fun info(caption: String = "Informação", message: String) { //  if (ui.windows.isNotEmpty())
    //   Notification(caption, message, Notification.Type.HUMANIZED_MESSAGE, true)
    //      .show(Page.getCurrent())
    //  else
    MessageBox.createInfo()
      .withCaption(caption)
      .withHtmlMessage(message)
      .withCloseButton(ButtonOption.caption("Fechar"))
      .asModal(true)
      .open()
  }

  fun warning(caption: String = "Aviso", message: String) {
    if (ui.windows.isNotEmpty()) Notification(caption, message, Notification.Type.WARNING_MESSAGE, true).apply {
      delayMsec = 2000
    }.show(Page.getCurrent())
    else MessageBox.createWarning()
      .withCaption(caption)
      .withHtmlMessage(message)
      .withCloseButton(ButtonOption.caption("Fechar"))
      .asModal(true)
      .open()
  }

  fun error(caption: String = "Erro", message: String) {
    if (ui.windows.isNotEmpty()) Notification(caption, message, Notification.Type.ERROR_MESSAGE, true).apply {
      delayMsec = 2000
    }.show(Page.getCurrent())
    else MessageBox.createError()
      .withCaption(caption)
      .withHtmlMessage(message)
      .withCloseButton(ButtonOption.caption("Fechar"))
      .asModal(true)
      .open()
  }

  fun question(caption: String = "Questão", message: String, execYes: () -> Unit = {}, execNo: () -> Unit = {}) {
    MessageBox.createQuestion()
      .withCaption(caption)
      .withHtmlMessage(message)
      .withYesButton(execYes, *arrayOf(ButtonOption.caption("Sim")))
      .withNoButton(execNo, *arrayOf(ButtonOption.caption("Não")))
      .asModal(true)
      .open()
  }

  fun question(
    caption: String = "Questão",
    message: Component,
    execYes: (Component) -> Unit = {},
    execNo: (Component) -> Unit = {},
              ) {
    MessageBox.createQuestion()
      .withCaption(caption)
      .withMessage(message)
      .withYesButton({ execYes(message) }, *arrayOf(ButtonOption.caption("Sim")))
      .withNoButton({ execNo(message) }, *arrayOf(ButtonOption.caption("Não")))
      .asModal(true)
      .open()
  }

  fun image(title: String, image: ByteArray) {
    val resource = image.makeResource()
    val embedded = Embedded()
    embedded.type = Embedded.TYPE_BROWSER
    embedded.source = resource
    MessageBox.create().withCaption(title).withMessage(embedded).withHeight("100%").withWidth("100%").open()
  }
}