package br.com.engecopi.framework.ui.view

import com.github.mvysny.karibudsl.v8.cssLayout
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isExpanded
import com.github.mvysny.karibudsl.v8.perc
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.data.BeanValidationBinder
import com.vaadin.shared.Registration
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme
import kotlin.reflect.KClass

open class DialogPopup<BEAN: Any>(caption: String, classBean: KClass<BEAN>): Window(caption) {
  val binder = BeanValidationBinder(classBean.java)
  val form = VerticalLayout().apply {
    setSizeFull()
  }
  private val btnOk: Button = Button("Confirma").apply {
    addStyleName(ValoTheme.BUTTON_PRIMARY)
  }
  private val btnCancel = Button("Cancela")
  private val toolBar = buildToolBar()

  init {
    isClosable = false
    isResizable = false
    isModal = true
  }

  fun show() {
    center()
    content = VerticalLayout(form, toolBar)
    addStyleName(ValoTheme.PANEL_WELL)
    UI.getCurrent()
      .addWindow(this)
  }

  fun initForm(configForm: (VerticalLayout) -> Unit) {
    configForm(form)
  }

  private fun buildToolBar(): Component {
    val espaco = Label()
    val tool = HorizontalLayout()
    tool.setWidth("100%")
    tool.isSpacing = true
    tool.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
    tool.addComponents(espaco, btnOk, btnCancel)
    tool.setExpandRatio(espaco, 1f)
    btnOk.addClickListener {this.btnOkClick()}
    btnCancel.addClickListener {this.btnCancelClick()}
    return tool
  }

  fun addClickListenerOk(listener: (Button.ClickEvent) -> Unit): Registration {
    return btnOk.addClickListener(listener)
  }

  fun addClickListenerCancel(listener: (Button.ClickEvent) -> Unit): Registration {
    return btnCancel.addClickListener(listener)
  }

  private fun btnCancelClick() {
    close()
  }

  private fun btnOkClick() {
    val status = binder.validate()
    if(!status.hasErrors()) close()
  }
}

fun VerticalLayout.grupo(caption: String = "", block: VerticalLayout.() -> Unit) {
  cssLayout(caption) {
    this.isExpanded = false
    w = 100.perc
    addStyleName(ValoTheme.LAYOUT_CARD)
    verticalLayout {
      w = 100.perc
      this.block()
      //this.forEach {it.w = 100.perc}
    }
  }
}

fun VerticalLayout.row(block: HorizontalLayout.() -> Unit) {
  horizontalLayout {
    isExpanded = true
    w = 100.perc
    this.block()
    this.forEach {it.w = 100.perc}
  }
}