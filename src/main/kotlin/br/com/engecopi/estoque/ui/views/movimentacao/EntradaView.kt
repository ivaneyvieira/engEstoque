package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.RegistryUserInfo.impressoraLocalizacao
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.print.PrintUtil.printText
import br.com.engecopi.estoque.viewmodel.movimentacao.EntradaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.EntradaVo
import br.com.engecopi.estoque.viewmodel.movimentacao.IEntradaView
import br.com.engecopi.estoque.viewmodel.movimentacao.ProdutoNotaVo
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.saci.QuerySaci
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.Binder
import com.vaadin.event.ShortcutAction
import com.vaadin.event.selection.MultiSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.ContentMode.HTML
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.*
import com.vaadin.ui.renderers.TextRenderer

@AutoView
class EntradaView : NotaView<EntradaVo, EntradaViewModel, IEntradaView>(customFooterLayout = true), IEntradaView {
  private lateinit var edtBarcode: TextField
  private lateinit var formBinder: Binder<EntradaVo>
  private lateinit var fieldNotaFiscal: TextField

  init {
    isStillShow = true
    viewModel = EntradaViewModel(this)
    layoutForm {
      if (operation == ADD) {
        binder.bean.lojaNF = lojaDeposito
        binder.bean.usuario = usuario
      }
      formLayout.apply {
        formBinder = binder
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px
        grupo("Nota fiscal de entrada") {
          row {
            fieldNotaFiscal = notaFiscalField(operation, binder)
            lojaField(operation, binder)
            comboBox<TipoNota>("Tipo") {
              expandRatio = 2f
              default { it.descricao }
              isReadOnly = true
              setItems(TipoNota.valuesEntrada())
              bind(binder).bind(EntradaVo::tipoNota)
            }
            textField("Rota") {
              expandRatio = 1f
              isReadOnly = true
              bind(binder).bind(EntradaVo::rota)
            }
          }
          row {
            textField("Observação") {
              expandRatio = 2f
              bind(binder).bind(EntradaVo::observacaoNota)
            }
          }
          row {
            dateField("Data") {
              expandRatio = 1f
              isReadOnly = true
              bind(binder).bind(EntradaVo::dataNota.name)
            }
            integerField("Número Interno") {
              expandRatio = 1f
              isReadOnly = true
              this.bind(binder).bind(EntradaVo::numeroInterno.name)
            }
            textField("Fornecedor") {
              expandRatio = 2f
              isReadOnly = true
              bind(binder).bind(EntradaVo::fornecedor.name)
            }
          }
        }
        row {
          label("<b>Produto</b>") {
            contentMode = HTML
          }
          edtBarcode = TextField("Código de Barras").apply {
            addValueChangeListener {
              val barcode = it.value
              gridProduto.execBarcode(barcode)
            }
            this.valueChangeMode = ValueChangeMode.EAGER
            this.addGlobalShortcutListener(ShortcutAction.KeyCode.F2) {
              this.focus()
            }

            if (!QuerySaci.test) {
              this.valueChangeMode = ValueChangeMode.LAZY
              valueChangeTimeout = 200
              this.blockCLipboard()
            }
          }

          addComponent(footerLayout)
          setComponentAlignment(footerLayout, Alignment.BOTTOM_LEFT)
          addComponent(edtBarcode)
          setComponentAlignment(edtBarcode, Alignment.BOTTOM_LEFT)
          addComponentsAndExpand(Label(""))
        }
        grupo {
          produtoField(operation, binder, "Entrada")
          gridProduto.editor.isEnabled = false
          gridProduto.addItemClickListener {
            if (it.mouseEventDetails.isDoubleClick && isAdmin) {
              val produto = it.item
              if (produto != null) openDilaogProduto(produto)
            }
          }
          gridProduto.addSelectionListener {
            if (it.isUserOriginated) {
              val produto = if (it is MultiSelectionEvent<ProdutoNotaVo>) {
                it.addedSelection.firstOrNull()
              }
              else {
                it.allSelectedItems.firstOrNull()
              }
              if (isAdmin) {
                if (produto != null) openDilaogProduto(produto)
              }
              else {
                if (produto != null) gridProduto.deselect(produto)
              }
            }
          }

          //gridProduto.setSelectionMode(Grid.SelectionMode.SINGLE)
          operationButton?.removeClickShortcut()
        }
      }
      if (!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Entrada de produtos")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      addOnly = !isAdmin
      column(EntradaVo::numeroNF) { //isSortable = true
        caption = "Número NF"
        setSortProperty("nota.numero")
      }
      grid.addComponentColumn { item ->
        val button = Button()
        button.isEnabled = isAdmin
        button.icon = VaadinIcons.PRINT
        button.addClickListener {
          item.itemNota?.recalculaSaldos()
          val numero = item.numeroNF
          showQuestion(msg = "Imprimir todos os itens da nota $numero?", execYes = {
            imprimeItem(item) {
              viewModel.imprimirNotaCompletaAgrupada(it)
            }
          }, execNo = {
            imprimeItem(item) {
              viewModel.imprimirItem(it)
            }
          })
        }
        button
      }.id = "btnPrint"
      column(EntradaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({ loja -> loja?.sigla ?: "" }, TextRenderer())
      }
      column(EntradaVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntradaVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(EntradaVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(EntradaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntradaVo::dataFabricacao) {
        caption = "Fabricação"
        mesAnoFormat()
        setSortProperty("dataFabricacao")
      }
      column(EntradaVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntradaVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntradaVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntradaVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntradaVo::localizacao) {
        caption = "Local"

        setRenderer({ it?.localizacao }, TextRenderer())
      }
      column(EntradaVo::usuario) {
        caption = "Usuário"
        setRenderer({ it?.loginName ?: "" }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntradaVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntradaVo::fornecedor) {
        caption = "Fornecedor"
        setSortProperty("nota.fornecedor")
      }
    }
  }

  private fun imprimeItem(domainObject: EntradaVo, imprimir: (ItemNota?) -> String) {
    val itemNota = domainObject.itemNota ?: domainObject.findEntity()
    val text = imprimir(itemNota)
    showZPLPreview(text) {
      printText(impressoraLocalizacao, text)
      refreshGrid()
    }
  }

  override fun processAdd(domainObject: EntradaVo) {
    super.processAdd(domainObject)
    imprimeItem(domainObject) { itemNota ->
      viewModel.imprimirNotaCompletaAgrupada(itemNota)
    }
  }

  override fun stillShow() {
    val bean = formBinder.bean
    if (gridProduto.editor.isOpen) gridProduto.editor.save()
    bean.entityVo = null
    bean.atualizaNota()
    formBinder.getBinding("produtos").ifPresent { binding ->
      binding.read(bean)
    }
    if (bean.produtosCompletos()) hideForm()
  }

  private fun openDilaogProduto(produtoItem: ProdutoNotaVo) {
    val nota = formBinder.bean.nota
    val dialog = DialogItemNota(nota, produtoItem) {
      gridProduto.selectionModel.select(produtoItem)
      gridProduto.dataProvider.refreshAll()
    }

    dialog.setWidth("400px")
    dialog.showDialog()
  }

  private fun Grid<ProdutoNotaVo>.execBarcode(barcode: String?) {
    if (!barcode.isNullOrBlank()) {
      val produtoItem = this.dataProvider.getAll().firstOrNull {
        val produto = it.produto
        produto.barcodeGtin.contains(barcode) || produto.codebar == barcode
      } ?: return

      this.selectionModel.select(produtoItem)
      produtoItem.selecionado = true

      openDilaogProduto(produtoItem)
    }
  }
}



