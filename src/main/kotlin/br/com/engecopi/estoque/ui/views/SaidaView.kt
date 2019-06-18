package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.NotaItens
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.ProdutoVO
import br.com.engecopi.estoque.viewmodel.SaidaViewModel
import br.com.engecopi.estoque.viewmodel.SaidaVo
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import br.com.engecopi.utils.IN
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.KeyShortcut
import com.github.mvysny.karibudsl.v8.ModifierKey
import com.github.mvysny.karibudsl.v8.VAlign
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.addGlobalShortcutListener
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.getAll
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.label
import com.github.mvysny.karibudsl.v8.perc
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutAction.KeyCode.F2
import com.vaadin.icons.VaadinIcons
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.shared.ui.ValueChangeMode.LAZY
import com.vaadin.ui.Alignment.BOTTOM_RIGHT
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.Notification
import com.vaadin.ui.TextField
import com.vaadin.ui.UI
import com.vaadin.ui.Window
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import org.vaadin.patrik.FastNavigation
import org.vaadin.viritin.fields.IntegerField

@AutoView("")
class SaidaView: NotaView<SaidaVo, SaidaViewModel>() {
  var formCodBar: PnlCodigoBarras? = null
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }

  init {
    viewModel = SaidaViewModel(this)
    layoutForm {
      if(operation == ADD) {
        binder.bean.lojaNF = lojaDefault
        binder.bean.usuario = usuario
        operationButton?.isEnabled = false
      }
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px
        grupo("Nota fiscal de saída") {
          verticalLayout {
            row {
              notaFiscalField(operation, binder)
              lojaField(operation, binder)
              comboBox<TipoNota>("Tipo") {
                expandRatio = 2f
                default {it.descricao}
                isReadOnly = true
                setItems(TipoNota.valuesSaida())
                bind(binder).bind(SaidaVo::tipoNota)
              }
              dateField("Data") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(SaidaVo::dataNota.name)
              }
              textField("Rota") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(SaidaVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expandRatio = 1f
                bind(binder).bind(SaidaVo::observacaoNota)
              }
            }
          }
        }

        grupo("Produto") {
          produtoField(operation, binder, "Saída")
        }
      }
      if(!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Saída de produtos")
    gridCrud {
      addCustomToolBarComponent(btnDesfazer())
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      addOnly = !isAdmin
      column(SaidaVo::numeroCodigoReduzido) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      grid.addComponentColumn {item ->
        Button().apply {
          val impresso = item?.entityVo?.impresso ?: true
          isEnabled = impresso == false || isAdmin
          icon = VaadinIcons.PRINT
          addClickListener {
            openText(viewModel.imprimir(item.itemNota))
            val print = item?.entityVo?.impresso ?: true
            it.button.isEnabled = print == false || isAdmin
            refreshGrid()
          }
        }
      }
        .id = "btnPrint"

      column(SaidaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja ->
                      loja?.sigla ?: ""
                    }, TextRenderer())
      }
      column(SaidaVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(SaidaVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(SaidaVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(SaidaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(SaidaVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(SaidaVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(SaidaVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(SaidaVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(SaidaVo::localizacao) {
        caption = "Localização"
        setRenderer({it?.abreviacao}, TextRenderer())
      }
      column(SaidaVo::usuario) {
        caption = "Usuário"
        setRenderer({
                      it?.loginName ?: ""
                    }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(SaidaVo::rotaDescricao) {
        caption = "Rota"
      }
      column(SaidaVo::cliente) {
        caption = "Cliente"
        setSortProperty("nota.cliente")
      }
      grid.setStyleGenerator {saida ->
        if(saida.status == CONFERIDA) "pendente"
        else null
      }
    }
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Código de barras") {key ->
      val nota = viewModel.processaKey(key)
      if(nota == null || nota.vazio) showError("A nota não foi encontrada")
      else {
        val dlg = DlgNotaSaida(nota, viewModel)
        dlg.showDialog()
        Thread.sleep(1000)
        dlg.focusEditor()
      }
    }
  }
}

class DlgNotaSaida(val nota: NotaItens, val viewModel: SaidaViewModel): Window("Nota de Saída") {
  private lateinit var gridProdutos: Grid<ProdutoVO>
  private val edtBarcode = TextField()

  fun focusEditor() {
    edtBarcode.focus()
  }

  init {
    addBlurListener {
      edtBarcode.focus()
    }
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
        .px

      grupo("Nota fiscal de saída") {
        verticalLayout {
          row {
            textField("Nota Fiscal") {
              expandRatio = 2f
              isReadOnly = true
              value = nota.nota?.numero ?: ""
              this.tabIndex = -1
            }
            textField("Loja") {
              expandRatio = 2f
              isReadOnly = true
              value = nota.nota?.loja?.sigla
              this.tabIndex = -1
            }
            textField("Tipo") {
              expandRatio = 2f
              isReadOnly = true
              value = nota.nota?.tipoNota?.descricao ?: ""
              this.tabIndex = -1
            }
            dateField("Data") {
              expandRatio = 1f
              isReadOnly = true
              value = nota.nota?.data
              this.tabIndex = -1
            }
            textField("Rota") {
              expandRatio = 1f
              isReadOnly = true
              value = nota.nota?.rota
              this.tabIndex = -1
            }
          }
          row {
            textField("Observação da nota fiscal") {
              expandRatio = 1f
              isReadOnly = true
              value = nota.nota?.observacao
              this.tabIndex = -1
            }
          }
        }
      }

      grupo {
        row {
          w = 100.perc
          label("Produto")
          edtBarcode.apply {
            addValueChangeListener {
              val barcode = it.value
              execBarcode(barcode)
            }
            this.addGlobalShortcutListener(F2) {
              focusEditor()
            }

            this.valueChangeMode = LAZY
            valueChangeTimeout = 200
            this.addGlobalShortcutListener(KeyShortcut(KeyCode.V, setOf(ModifierKey.Ctrl))) {
              this.value = ""
            }
            this.addContextClickListener {
              this.value = ""
            }
          }
          this.addComponentsAndExpand(edtBarcode)
        }

        row {
          gridProdutos = grid(ProdutoVO::class) {
            this.tabIndex = -1
            val abreviacao = RegistryUserInfo.abreviacaoDefault
            //nota.refresh()
            val itens = nota.itens.filter {it.localizacao.startsWith(abreviacao)}

            this.dataProvider = ListDataProvider(itens.map {item ->
              ProdutoVO(item.produto, item.tipoMov ?: SAIDA, LocProduto(item.localizacao), item.id != 0L).apply {
                this.quantidade = item.quantidade
                this.value = item
              }
            })
            removeAllColumns()
            val selectionModel = setSelectionMode(MULTI)
            selectionModel.addSelectionListener {select ->
              if(select.isUserOriginated) {
                this.dataProvider.getAll()
                  .forEach {
                    it.selecionado = false
                  }
                select.allSelectedItems.forEach {
                  if(it.saldoFinal < 0) {
                    Notification.show("Saldo Insuficiente")
                    selectionModel.deselect(it)
                    it.selecionado = false
                  }
                  else if(!it.editavel()) {
                    Notification.show("Não editavel")
                    selectionModel.deselect(it)
                    it.selecionado = false
                  }
                  else if(!RegistryUserInfo.userDefaultIsAdmin) {
                    Notification.show("Usuário não é administrador")
                    selectionModel.deselect(it)
                    it.selecionado = true
                  }
                  else it.selecionado = true
                }
              }
            }
            editor.isEnabled = true
            val comboLoc = ComboBox<LocProduto>().apply {
              isEmptySelectionAllowed = false
              isTextInputAllowed = false
            }
            val edtQuant = IntegerField().apply {
              w = 100.px
              addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT)
            }
            setSizeFull()
            addColumnFor(ProdutoVO::codigo) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ProdutoVO::descricaoProduto) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ProdutoVO::localizacao) {
              expandRatio = 4
              caption = "Localização"
              setEditorComponent(comboLoc)
            }
            addColumnFor(ProdutoVO::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ProdutoVO::quantidade) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = VAlign.Right
              setEditorComponent(edtQuant)
            }
            addColumnFor(ProdutoVO::saldoFinal) {
              expandRatio = 1
              caption = "Saldo"
              align = VAlign.Right
            }
            editor.addOpenListener {event ->
              event.bean.produto?.let {produto ->
                val locSulfixos = produto.localizacoes()
                  .map {LocProduto(it)}
                comboLoc.setItems(locSulfixos)
                comboLoc.setItemCaptionGenerator {it.localizacao}
                comboLoc.value = event.bean.localizacao
              }
              comboLoc.isReadOnly = !event.bean.editavel()
              edtQuant.isReadOnly = !event.bean.editavel()
            }
            val nav = FastNavigation<ProdutoVO>(this, false, true)
            nav.changeColumnAfterLastRow = true
            nav.openEditorWithSingleClick = true
            nav.allowArrowToChangeRow = true
            nav.openEditorOnTyping = true
            nav.addEditorSaveShortcut(KeyCode.ENTER)
            editor.cancelCaption = "Cancelar"
            editor.saveCaption = "Salvar"
            editor.isBuffered = false
            this.setStyleGenerator {
              if(it.saldoFinal < 0) "error_row"
              else if(!it.editavel()) "ok"
              else null
            }
          }
        }
      }

      row {
        horizontalLayout {
          alignment = BOTTOM_RIGHT
          button("Cancela") {
            alignment = BOTTOM_RIGHT
            addClickListener {
              close()
            }
          }
          button("Confirma") {
            alignment = BOTTOM_RIGHT
            addStyleName(ValoTheme.BUTTON_PRIMARY)
            addClickListener {
              val itens = gridProdutos.selectedItems.toList()
                .filter {it.saldoFinal >= 0 && it.editavel()}
              val naoSelect = gridProdutos.dataProvider.getAll()
                .minus(itens)
                .filter {it.editavel()}

              viewModel.confirmaProdutos(itens, CONFERIDA)
              viewModel.confirmaProdutos(naoSelect, ENT_LOJA)
              close()
            }
          }
        }
      }
    }
  }

  private fun execBarcode(barcode: String?) {
    if(!barcode.isNullOrBlank()) {
      val produto = viewModel.processaBarcodeProduto(barcode)
      if(produto == null) viewModel.view.showWarning("Produto não encontrado no saci")
      else {
        val produtosVO = gridProdutos.dataProvider.getAll()
        val produtos = produtosVO.mapNotNull {it.value?.produto}
        if(produtos.contains(produto)) {
          val itemVO = produtosVO.filter {it.value?.produto?.id == produto.id}
          itemVO.forEach {item ->
            val codigo = item.value?.codigo ?: "Não encontrado"
            if(item.saldoFinal < 0) viewModel.view.showWarning("O saldo final do produto $codigo está negativo")
            else if(!item.editavel()) viewModel.view.showWarning("O produto $codigo não é editável")
            else gridProdutos.select(item)
          }
        }
        else viewModel.view.showWarning("Produto não encontrado no grid")
      }
      edtBarcode.focus()
      edtBarcode.selectAll()
    }
  }
}

private fun ProdutoVO.editavel(): Boolean {
  val itemNota = value ?: return false
  return itemNota.status.IN(INCLUIDA, ENT_LOJA)
}
