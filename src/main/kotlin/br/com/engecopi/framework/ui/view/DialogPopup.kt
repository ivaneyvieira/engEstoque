package br.com.engecopi.framework.ui.view

import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.BeanValidationBinder
import com.vaadin.shared.Registration
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import kotlin.reflect.KClass

open class DialogPopup<BEAN : Any>(caption: String, classBean: KClass<BEAN>) : Window(caption) {
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
    UI.getCurrent().addWindow(this)
    this.isResponsive = true
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
    btnOk.addClickListener { this.btnOkClick() }
    btnCancel.addClickListener { this.btnCancelClick() }
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
    if (!status.hasErrors()) close()
  }
}

fun VerticalLayout.grupo(caption: String? = null, expand: Boolean = false, block: VerticalLayout.() -> Unit) {
  val cssLayout = cssLayout(caption) {
    if (expand) h = 100.perc
    w = 100.perc
    addStyleName(ValoTheme.LAYOUT_CARD)
    verticalLayout {
      w = 100.perc
      this.block()
    }
  }
  if (expand) setExpandRatio(cssLayout, 1f)
}

fun VerticalLayout.row(expand: Boolean = false, block: HorizontalLayout.() -> Unit) {
  horizontalLayout {
    this.block()
    this.forEach {
      if (it.expandRatio > 0f) it.w = 100.perc
    }
    isExpanded = expand
    if (expand) (this.parent as? VerticalLayout)?.addComponentsAndExpand(this)
    w = 100.perc
  }
}