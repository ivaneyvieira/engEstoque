package br.com.engecopi.framework.ui.view

import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.DELETE
import br.com.engecopi.framework.ui.view.CrudOperation.READ
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.QueryView
import br.com.engecopi.framework.viewmodel.Sort
import com.github.mvysny.karibudsl.v8.VaadinDsl
import com.github.mvysny.karibudsl.v8.addGlobalShortcutListener
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.init
import com.github.mvysny.karibudsl.v8.w
import com.github.mvysny.karibudsl.v8.wrapContent
import com.vaadin.data.BeanValidationBinder
import com.vaadin.data.Binder
import com.vaadin.data.provider.CallbackDataProvider
import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.Query
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.Resource
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.Alignment.BOTTOM_LEFT
import com.vaadin.ui.Alignment.BOTTOM_RIGHT
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.CssLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.HasComponents
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Layout
import com.vaadin.ui.Notification
import com.vaadin.ui.TextField
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme
import org.vaadin.grideditorcolumnfix.GridEditorColumnFix
import java.awt.Event.ENTER
import java.util.stream.*
import kotlin.collections.set
import kotlin.reflect.KProperty1
import kotlin.streams.toList

abstract class CrudLayoutView<C: EntityVo<*>, V: CrudViewModel<*, *, C>>: LayoutView<V>() {
  var isAddClose = true
  val headerLayout = HorizontalLayout()
  val toolbarLayout = CssLayout()
  val filterLayout = HorizontalLayout()
  var formWindow: Window? = null
  val windowCaptions = HashMap<CrudOperation, String>()
  val deletedMessage = "Registro apagado"
  val savedMessage = "Registro gravado"
  val findAllButton = Button("").apply {
    addClickListener {findAllButtonClicked()}
    description = "Atualizar"
    icon = VaadinIcons.REFRESH
    addToolbarComponent(this)
  }
  val addButton = Button("").apply {
    addClickListener {addButtonClicked()}
    description = "Adiconar"
    icon = VaadinIcons.PLUS
    addToolbarComponent(this)
  }
  val updateButton = Button("").apply {
    addClickListener {updateButtonClicked()}
    description = "Modificar"
    icon = VaadinIcons.PENCIL
    addToolbarComponent(this)
  }
  val deleteButton = Button("").apply {
    addClickListener {deleteButtonClicked()}
    description = "Apagar"
    icon = VaadinIcons.TRASH
    addToolbarComponent(this)
  }
  val readButton = Button("").apply {
    addClickListener {readButtonClicked()}
    description = "Read"
    icon = VaadinIcons.SEARCH
    addToolbarComponent(this)
  }
  var queryOnly: Boolean = false
    set(value) {
      field = value

      findAllButton.isVisible = true
      addButton.isVisible = !value
      updateButton.isVisible = !value
      readButton.isVisible = value
      deleteButton.isVisible = !value
    }
  var addOnly: Boolean = false
    set(value) {
      field = value

      findAllButton.isVisible = true
      addButton.isVisible = true
      updateButton.isVisible = !value
      readButton.isVisible = value
      deleteButton.isVisible = !value
    }
  var reloadOnly: Boolean = false
    set(value) {
      field = value

      findAllButton.isVisible = true
      addButton.isVisible = !value
      updateButton.isVisible = !value
      readButton.isVisible = value
      deleteButton.isVisible = !value
    }
  //val domainType get() = viewModel.crudClass
  var layoutForm: (CrudForm<C>) -> Unit = {}
  val find = CallbackDataProvider.FetchCallback<C, String> {query ->
    findQuery(query)
  }
  val count = CallbackDataProvider.CountCallback<C, String> {query ->
    countQuery(query)
  }
  private val dataLazyFilterProvider = DataProvider.fromFilteringCallbacks(find, count)
    .withConfigurableFilter()
  private val filtroEdt = TextField("Pesquisa") {
    val value = if(it.value.isNullOrBlank()) null else it.value
    dataLazyFilterProvider.setFilter(value)
    dataLazyFilterProvider.refreshAll()
  }
  val grid = Grid<C>().apply {
    GridEditorColumnFix(this)
    setSizeFull()
    addSelectionListener {gridSelectionChanged()}

    this.addGlobalShortcutListener(ENTER) {
      if(this.selectedItems.isNotEmpty()) if(updateButton.isVisible) updateButtonClicked()
      else readButtonClicked()
    }
    this.addItemClickListener {e ->
      if(e.mouseEventDetails.isDoubleClick) if(!this.asSingleSelect().isEmpty) if(updateButton.isVisible) updateButtonClicked()
      else readButtonClicked()
    }

    this.dataProvider = dataLazyFilterProvider
  }

  override fun form(titleForm: String, block: (@VaadinDsl VerticalLayout).() -> Unit) {
    form(titleForm)
  }

  fun form(titleForm: String) {
    super.form(titleForm) {
      headerLayout.isSpacing = true
      headerLayout.defaultComponentAlignment = BOTTOM_LEFT

      toolbarLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP)
      headerLayout.addComponent(toolbarLayout)

      filterLayout.isSpacing = true
      headerLayout.addComponent(filterLayout)
      addFilterComponent(filtroEdt)
      this.addComponent(headerLayout)
      grid.setSizeFull()
      this.addComponentsAndExpand(grid)
    }
  }

  init {
    this.setSizeFull()
    this.setMargin(false)
    this.isSpacing = true

    windowCaptions[ADD] = "Adicionar"
    windowCaptions[UPDATE] = "Atualisar"
    windowCaptions[DELETE] = "Apagar"

    updateButtons()
  }

  override fun attach() {
    super.attach()
    refreshGrid()
  }

  var addOperationVisible
    get() = addButton.isVisible
    set(value) {
      addButton.isVisible = value
    }
  var updateOperationVisible
    get() = updateButton.isVisible
    set(value) {
      updateButton.isVisible = value
    }
  var deleteOperationVisible
    get() = deleteButton.isVisible
    set(value) {
      deleteButton.isVisible = value
    }
  var findAllOperationVisible
    get() = findAllButton.isVisible
    set(value) {
      findAllButton.isVisible = value
    }

  fun refreshGrid() {
    grid.dataProvider?.refreshAll()
  }

  private fun updateButtons() {
    val rowSelected = !(grid.asSingleSelect()?.isEmpty ?: true)
    updateButton.isEnabled = rowSelected
    deleteButton.isEnabled = rowSelected
  }

  fun layoutForm(crudForm: CrudForm<C>.() -> Unit) {
    this.layoutForm = crudForm
  }

  fun buildNewForm(operation: CrudOperation,
                   domainObject: C,
                   readOnly: Boolean,
                   cancelButtonClickListener: (CrudForm<C>) -> Unit,
                   operationButtonClickListener: (CrudForm<C>) -> Unit): CrudForm<C> {
    return CrudForm(operation,
                    domainObject,
                    readOnly,
                    cancelButtonClickListener,
                    operationButtonClickListener,
                    layoutForm)
  }

  private fun gridSelectionChanged() {
    updateButtons()
  }

  protected fun findAllButtonClicked() {
    grid.asSingleSelect()
      ?.clear()
    refreshGrid()
  }

  fun itemContains(item: C?): Boolean {
    return false
    //    item ?: return false
    //  return viewModel.existsBean(item)
  }

  private fun readButtonClicked() {
    val domainObject = grid.asSingleSelect()?.value ?: return
    showForm(READ, domainObject, true, savedMessage) {
      viewModel.crudBean = domainObject
      viewModel.read()
    }
  }

  private fun addButtonClicked() {
    viewModel.cleanBean()
    val domainObject = viewModel.crudBean ?: return
    showForm(ADD, domainObject, false, savedMessage) {
      viewModel.crudBean = domainObject
      viewModel.add()
    }
  }

  fun updateButtonClicked() {
    val domainObject = grid.asSingleSelect()?.value ?: return
    showForm(UPDATE, domainObject, false, savedMessage) {
      viewModel.crudBean = domainObject
      viewModel.update()
    }
  }

  fun deleteButtonClicked() {
    val domainObject = grid.asSingleSelect()?.value ?: return
    showForm(DELETE, domainObject, true, deletedMessage) {
      viewModel.crudBean = domainObject
      viewModel.delete()
    }
  }

  fun showForm(operation: CrudOperation,
               domainObject: C,
               readOnly: Boolean,
               successMessage: String,
               buttonClickListener: () -> Unit) {
    fun operation(form: CrudForm<C>) {
      buttonClickListener()
      if(operation != ADD || isAddClose) hideForm()
      else {
        viewModel.cleanBean()
        form.binder.bean = viewModel.crudBean
        form.focusFirst()
      }
      if(viewModel.resultadoOK) Notification.show(successMessage)
    }

    fun cancel(form: CrudForm<C>) {
      val selected = grid.asSingleSelect()
        ?.value
      hideForm()
      grid.asSingleSelect()
        ?.clear()
      grid.asSingleSelect()
        ?.value = selected
    }

    val form = buildNewForm(operation, domainObject, readOnly, ::cancel, ::operation)

    showForm(operation, form)
  }

  fun addCustomToolBarComponent(customToolBarComponent: Component) {
    addToolbarComponent(customToolBarComponent)
  }

  fun addCustomFormComponent(customFormComponent: Component?) {
    customFormComponent ?: return
    addFormComponent(customFormComponent)
  }

  fun addFilterComponent(component: Component) {
    filterLayout.addComponent(component)
  }

  fun addToolbarComponent(component: Component) {
    toolbarLayout.addComponent(component)
  }

  fun addFormComponent(component: Component) {
    headerLayout.addComponentsAndExpand(component)
  }

  fun showWindow(caption: String?, form: Component) {
    val windowLayout = VerticalLayout(form)
    //windowLayout.setWidth("100%")
    windowLayout.setMargin(false)
    formWindow = Window(caption, windowLayout).apply {
      w = wrapContent
      isClosable = true
      isResizable = false
      isModal = true
      styleName = "modal"
      addCloseShortcut(KeyCode.ESCAPE)
      this.setWidth("80%")
      this.isModal = true
      this.center()
    }

    UI.getCurrent()
      .addWindow(formWindow)
  }

  fun showForm(operation: CrudOperation, form: Component) {
    //if(operation != READ) {
    showWindow(windowCaptions[operation], form)
    //}
  }

  fun hideForm() {
    formWindow?.close()
  }

  private fun findQuery(query: Query<C, String>): Stream<C> {
    viewModel.updateQueryView(query.viewQuery())
    return viewModel.findQuery()
      .stream()
  }

  private fun countQuery(query: Query<C, String>): Int {
    viewModel.updateQueryView(query.viewQuery())
    return viewModel.countQuery()
  }

  fun HasComponents.gridCrud(block: Grid<C>.() -> Unit = {}): Grid<C> {
    setSizeFull()
    grid.removeAllColumns()
    return init(grid) {
      expandRatio = 1f
      block()
    }
  }

  fun <T> Grid<C>.column(property: KProperty1<C, T>, block: Column<C, T?>.() -> Unit = {}): Column<C, T?> {
    val column: Column<C, T?> = addColumn(property)
    column.isMinimumWidthFromContent = true
    column.block()
    return column
  }

  override fun updateView() {
    dataLazyFilterProvider.refreshAll()
    refreshGrid()
    val bean = viewModel.crudBean
    if(itemContains(bean)) {
      grid.asSingleSelect()
        ?.value = bean
      // TODO: grid.scrollTo(addedObject);
    }
  }

  override fun updateModel() {
  }
}

fun <T> Query<T, String>.viewQuery(): QueryView {
  val sorts = this.sortOrders.map {
    Sort(it.sorted, it.direction == SortDirection.DESCENDING)
  }
  return QueryView(this.offset, this.limit, this.filter.orElse(""), sorts)
}

class CrudForm<C: EntityVo<*>>(val operation: CrudOperation,
                               val domainObject: C,
                               val readOnly: Boolean,
                               cancelButtonClickListener: (CrudForm<C>) -> Unit,
                               operationButtonClickListener: (CrudForm<C>) -> Unit,
                               layoutForm: (CrudForm<C>) -> Unit): VerticalLayout() {
  private val domainClass = domainObject.javaClass
  val binder: Binder<C> = BeanValidationBinder<C>(domainClass).apply {
    bean = domainObject
  }
  val buttonCaptions = HashMap<CrudOperation, String>()
  val buttonIcons = HashMap<CrudOperation, Resource?>()
  val buttonStyleNames = HashMap<CrudOperation, String?>()
  val formLayout = VerticalLayout()
  var operationButton: Button? = null

  init {
    updateButtons()
    val footerLayout = buildFooter(operation, cancelButtonClickListener, operationButtonClickListener)

    layoutForm(this)
    formLayout.setSizeFull()
    addComponentsAndExpand(formLayout)

    addComponent(footerLayout)
    setComponentAlignment(footerLayout, BOTTOM_RIGHT)
    setMargin(true)
  }

  fun focusFirst() {
    val field = binder.fields.toList().firstOrNull {it is Component.Focusable} as?  Component.Focusable
    field?.focus()
  }

  fun updateButtons() {
    buttonCaptions[READ] = "Confirma"
    buttonCaptions[ADD] = "Adiciona"
    buttonCaptions[UPDATE] = "Atualiza"
    buttonCaptions[DELETE] = "Apaga"

    buttonIcons[READ] = null
    buttonIcons[ADD] = VaadinIcons.CHECK
    buttonIcons[UPDATE] = VaadinIcons.CHECK
    buttonIcons[DELETE] = VaadinIcons.TRASH

    buttonStyleNames[READ] = null
    buttonStyleNames[ADD] = ValoTheme.BUTTON_PRIMARY
    buttonStyleNames[UPDATE] = ValoTheme.BUTTON_PRIMARY
    buttonStyleNames[DELETE] = ValoTheme.BUTTON_DANGER
  }

  private fun buildFooter(operation: CrudOperation,
                          cancelButtonClickListener: (CrudForm<C>) -> Unit,
                          operationButtonClickListener: (CrudForm<C>) -> Unit): Layout {
    operationButton = buildOperationButton(operation, operationButtonClickListener)
    val cancelButton = buildCancelButton(cancelButtonClickListener)
    val footerLayout = HorizontalLayout()
    footerLayout.setSizeUndefined()
    footerLayout.isSpacing = true

    footerLayout.addComponent(cancelButton)

    footerLayout.addComponent(operationButton)

    return footerLayout
  }

  private fun buildOperationButton(operation: CrudOperation, clickListener: (CrudForm<C>) -> Unit): Button {
    val caption = buttonCaptions[operation]
    val button = Button(caption, buttonIcons[operation])
    button.setClickShortcut(KeyCode.ENTER)
    button.addStyleName(buttonStyleNames[operation])
    button.addClickListener {_ ->
      val validate = binder.validate()
      if(validate.isOk) clickListener(this)
      else Notification.show(validate.beanValidationErrors.joinToString {it.errorMessage})
    }
    return button
  }

  private fun buildCancelButton(clickListener: (CrudForm<C>) -> Unit): Button {
    val button = Button("Cancela")
    button.addClickListener {clickListener(this)}
    button.setClickShortcut(KeyCode.ESCAPE)
    return button
  }
}


