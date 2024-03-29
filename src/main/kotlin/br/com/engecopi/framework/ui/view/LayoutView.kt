package br.com.engecopi.framework.ui.view

import br.com.engecopi.framework.viewmodel.ViewModel
import br.com.engecopi.utils.ZPLPreview
import com.fo0.advancedtokenfield.main.AdvancedTokenField
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.Binder
import com.vaadin.data.Binder.Binding
import com.vaadin.data.HasItems
import com.vaadin.data.HasValue
import com.vaadin.data.ReadOnlyHasValue
import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.navigator.View
import com.vaadin.ui.*
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.renderers.LocalDateRenderer
import com.vaadin.ui.renderers.LocalDateTimeRenderer
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.themes.ValoTheme
import org.tepi.listbuilder.ListBuilder
import org.vaadin.addons.filteringgrid.FilterGrid
import org.vaadin.viritin.fields.*
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.streams.toList

abstract class LayoutView<V : ViewModel<*>> : VerticalLayout(), View {
  lateinit var viewModel: V

  init {
    this.setSizeFull()
  }

  open fun form(titleForm: String, block: (@VaadinDsl VerticalLayout).() -> Unit = {}) {
    isMargin = true
    this.title(titleForm)
    this.block()
  }

  fun <T> Grid<T>.actionSelected(msgErro: String = "Selecione um item", action: (T) -> Unit) {
    this.selectedItems.firstOrNull()?.let { item -> action(item) } ?: showWarning(msgErro)
  }

  fun showWarning(msg: String) {
    if (msg.isNotBlank()) MessageDialog.warning(message = msg)
  }

  fun showError(msg: String) {
    if (msg.isNotBlank()) MessageDialog.error(message = msg)
  }

  fun showInfo(msg: String) {
    if (msg.isNotBlank()) MessageDialog.info(message = msg)
  }

  fun showImage(title: String, image: ByteArray, printRunnable: () -> Unit) {
    MessageDialog.image(title, image, printRunnable)
  }

  fun showZPLPreview(zplCode: String, printRunnable: () -> Unit) {
    val image = ZPLPreview.createPdf(zplCode, "4x2")
    if (image != null) showImage("Preview", image, printRunnable)
  }

  fun showQuestion(msg: String, execYes: () -> Unit, execNo: () -> Unit) {
    if (msg.isNotBlank()) MessageDialog.question(message = msg, execYes = execYes, execNo = execNo)
  }
}

fun <T> ComboBox<T>.default(valueEmpty: T? = null, captionGenerator: (T) -> String = { it.toString() }) {
  isEmptySelectionAllowed = false
  isTextInputAllowed = false
  valueEmpty?.let {
    this.emptySelectionCaption = "Todas"
    isEmptySelectionAllowed = true
  }
  setItemCaptionGenerator(captionGenerator)
}

fun <V, T> HasItems<T>.bindItens(binder: Binder<V>, propertyList: String): Binding<V, Collection<T>> {
  val hasValue = (this as? HasValue<*>)
  val itensOld: List<T>? = (this.dataProvider as? ListDataProvider<T>)?.items?.toList()

  return bind<V, Collection<T>>(binder, propertyList) { itens ->
    val oldValue = hasValue?.value
    if (itensOld != itens) {
      if (this is ComboBox<T>) setItems({ itemCaption, filterText ->
        itemCaption.toUpperCase().startsWith(filterText.toUpperCase())
      }, itens)
      else if (this is TwinColSelect<T>) setItems(itens)
      else setItems(itens)
    }
    @Suppress("UNCHECKED_CAST") val contains = itens.contains(oldValue as? T)
    val value = if (oldValue == null || !contains) null
    else itens.find { it == oldValue }
    if (value == null) hasValue?.value = hasValue?.emptyValue
    else hasValue?.value = value
  }
}

fun <V, T> TwinColSelect<T>.bindItensSet(binder: Binder<V>, propertyList: String) {
  bind<V, MutableSet<T>>(binder, propertyList) { itens ->
    value = emptySet()
    setItems(itens)
  }
}

fun <BEAN> HasValue<*>.bindReadOnly(binder: Binder<BEAN>, property: String, block: (Boolean) -> Unit = {}) {
  bind<BEAN, Boolean>(binder, property) { readOnly ->
    isReadOnly = readOnly
    block(readOnly)
  }
}

fun <BEAN> Component.bindVisible(binder: Binder<BEAN>, property: String, block: (Boolean) -> Unit = {}) {
  bind<BEAN, Boolean>(binder, property) { visible ->
    isVisible = visible
    block(visible)
  }
}

fun <BEAN> Component.bindCaption(binder: Binder<BEAN>, property: String, block: (String) -> Unit = {}) {
  bind<BEAN, String>(binder, property) {
    caption = it
    block(it)
  }
}

private fun <BEAN, FIELDVALUE> bind(
  binder: Binder<BEAN>,
  property: String,
  blockBinder: (FIELDVALUE) -> Unit,
): Binding<BEAN, FIELDVALUE> {
  val field = ReadOnlyHasValue<FIELDVALUE> { itens -> blockBinder(itens) }
  return field.bind(binder).bind(property)
}

fun Binder<*>.reload() {
  this.bean = bean
}

inline fun <reified BEAN : Any, FIELDVALUE> HasValue<FIELDVALUE>.reloadBinderOnChange(
  binder: Binder<BEAN>,
  vararg propertys: KProperty1<BEAN, *>,
) {
  addValueChangeListener { event ->
    if (event.isUserOriginated && (event.oldValue != event.value)) {
      val bean = binder.bean
      if (propertys.isEmpty()) {
        val bindings = BEAN::class.memberProperties.mapNotNull { prop ->
          binder.getBinding(prop.name).orElse(null)
        }
        binder.fields.toList().mapNotNull { field ->
          bindings.find { binding ->
            binding.field == field && binding.field != this
          }
        }.forEach { binding ->
          binding.read(bean)
        }
      } else {
        reloadPropertys(binder, *propertys)
      }
    }
  }
}

fun <BEAN> reloadPropertys(binder: Binder<BEAN>, vararg propertys: KProperty1<BEAN, *>) {
  val bean = binder.bean
  propertys.forEach { prop ->
    binder.getBinding(prop.name).ifPresent { binding ->
      binding.read(bean)
    }
  }
}

fun <C> Column<C, LocalDate?>.dateFormat() {
  this.setRenderer(LocalDateRenderer { DateTimeFormatter.ofPattern("dd/MM/yy") })
}

fun <C> Column<C, LocalDate?>.mesAnoFormat() {
  this.setRenderer(LocalDateRenderer { DateTimeFormatter.ofPattern("MM/yy") })
}

fun <C> Column<C, LocalDate>.dateFormatNotNull() {
  this.setRenderer(LocalDateRenderer { DateTimeFormatter.ofPattern("dd/MM/yy") })
}

fun <C> Column<C, LocalDateTime?>.timeFormat() {
  this.setRenderer(LocalDateTimeRenderer { DateTimeFormatter.ofPattern("HH:mm") })
}

fun <C> Column<C, Int?>.intFormat() {
  setRenderer(NumberRenderer(DecimalFormat("0")))
  align = VAlign.Right
}

fun HasComponents.integerField(caption: String = "", block: IntegerField.() -> Unit = {}) =
  init(IntegerField(caption), block).apply {
    addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT)
  }

fun HasComponents.doubleField(caption: String = "", block: DoubleField.() -> Unit = {}) =
  init(DoubleField(caption), block)

fun HasComponents.emailField(caption: String = "", block: EmailField.() -> Unit = {}) = init(EmailField(caption), block)

fun HasComponents.clearableTextField(caption: String = "", block: ClearableTextField.() -> Unit = {}) =
  init(ClearableTextField(caption), block)

fun <T> HasComponents.headerField(caption: String = "", block: HeaderField<T>.() -> Unit = {}) =
  init(HeaderField(caption), block)

fun HasComponents.integerSliderField(captionPar: String = "", block: IntegerSliderField.() -> Unit = {}) =
  init(IntegerSliderField(), block).apply {
    this.caption = captionPar
  }

fun HasComponents.mCheckBox(captionPar: String = "", block: MCheckBox.() -> Unit = {}) =
  init(MCheckBox(), block).apply {
    this.caption = captionPar
  }

fun HasComponents.mTextField(captionPar: String = "", block: MTextField.() -> Unit = {}) =
  init(MTextField(), block).apply {
    this.caption = captionPar
  }

fun HasComponents.tokenField(captionPar: String = "", block: AdvancedTokenField.() -> Unit = {}) =
  init(AdvancedTokenField(), block).apply {
    this.caption = captionPar
  }

fun <T> HasComponents.labelField(caption: String = "", block: LabelField<T>.() -> Unit = {}) =
  init(LabelField(caption), block)

inline fun <reified T : Enum<*>> HasComponents.enumSelect(
  caption: String = "",
  noinline block: EnumSelect<T>.() -> Unit = {},
) = init(EnumSelect<T>(caption, T::class.java), block)

fun HasComponents.title(title: String) = label(title) {
  w = fillParent
  addStyleNames(ValoTheme.LABEL_LARGE, ValoTheme.LABEL_COLORED, ValoTheme.LABEL_BOLD)
}

//FilterGrid
fun <T : Any> (@VaadinDsl HasComponents).filterGrid(
  itemClass: KClass<T>? = null,
  caption: String? = null,
  dataProvider: DataProvider<T, *>? = null,
  block: (@VaadinDsl FilterGrid<T>).() -> Unit = {},
) = init(if (itemClass == null) FilterGrid() else FilterGrid<T>(itemClass.java)) {
  this.caption = caption
  if (dataProvider != null) this.dataProvider = dataProvider
  block()
}

@VaadinDsl
fun (@VaadinDsl HasComponents).listBuilder(
  caption: String? = null,
  block: (@VaadinDsl ListBuilder).() -> Unit = {},
) = init(ListBuilder(caption), block)

fun Window.showDialog() {
  isClosable = true
  isResizable = false
  isModal = true
  styleName = "modal" //isTabStopEnabled=true
  tabIndex = -1
  addCloseShortcut(KeyCode.ESCAPE)
  UI.getCurrent().addWindow(this)
  center()
}

fun (@VaadinDsl Component).expand(ratio: Int = 1) {
  this.expandRatio = ratio * 1f
  this.setSizeFull()
}