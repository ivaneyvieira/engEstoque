package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.Nota
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
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.VAlign
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.getAll
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.icons.VaadinIcons
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Alignment.BOTTOM_RIGHT
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.Window
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import org.vaadin.patrik.FastNavigation

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
                expand = 2
                default {it.descricao}
                isReadOnly = true
                setItems(TipoNota.valuesSaida())
                bind(binder).bind(SaidaVo::tipoNota)
              }
              dateField("Data") {
                expand = 1
                isReadOnly = true
                bind(binder).bind(SaidaVo::dataNota.name)
              }
              textField("Rota") {
                expand = 1
                isReadOnly = true
                bind(binder).bind(SaidaVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expand = 1
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
      column(SaidaVo::dataNota) {
        caption = "Data"
        dateFormat()
        setSortProperty("nota.data", "data", "hora")
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

  private fun btnLerChaveNota(): Button {
    return button("Ler Código") {
      icon = VaadinIcons.BARCODE
      addClickListener {
        readString("Código de barras", true) {_, key ->
          val nota = viewModel.processaKey(key)
          if(nota == null) showError("A nota não foi encontrada")
          else {
            val dlg = DlgNotaSaida(nota, viewModel)
            dlg.showDialog()
          }
          return@readString null
        }
      }
    }
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Código de barras") {key ->
      val nota = viewModel.processaKey(key)
      if(nota == null) showError("A nota não foi encontrada")
      else {
        val dlg = DlgNotaSaida(nota, viewModel)
        dlg.showDialog()
      }
    }
  }
}

class DlgNotaSaida(val nota: Nota, val viewModel: SaidaViewModel): Window("Nota de Saída") {
  private lateinit var gridProdutos: Grid<ProdutoVO>

  init {
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
        .px

      grupo("Nota fiscal de saída") {
        verticalLayout {
          row {
            textField("Nota Fiscal") {
              expand = 2
              isReadOnly = true
              value = nota.numero
            }
            textField("Loja") {
              expand = 2
              isReadOnly = true
              value = nota.loja?.sigla
            }
            textField("Tipo") {
              expand = 2
              isReadOnly = true
              value = nota.tipoNota?.descricao ?: ""
            }
            dateField("Data") {
              expand = 1
              isReadOnly = true
              value = nota.data
            }
            textField("Rota") {
              expand = 1
              isReadOnly = true
              value = nota.rota
            }
          }
          row {
            textField("Observação da nota fiscal") {
              expand = 1
              isReadOnly = true
              value = nota.observacao
            }
          }
        }
      }

      grupo("Produto") {
        row {
          gridProdutos = grid(ProdutoVO::class) {
            val abreviacao = RegistryUserInfo.abreviacaoDefault
            //nota.refresh()
            val itens = nota.itensNota().filter {it.status == INCLUIDA}
              .filter {it.localizacao.startsWith(abreviacao)}

            this.dataProvider = ListDataProvider(itens.map {item ->
              ProdutoVO(item.produto, item.tipoMov ?: SAIDA, LocProduto(item.localizacao)).apply {
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
                  if(it.saldoFinal < 0){
                    Notification.show("Saldo Insuficiente")
                    selectionModel.deselect(it)
                    it.selecionado = false
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
            }
            addColumnFor(ProdutoVO::saldoFinal) {
              expandRatio = 1
              caption = "Saldo"
              align = VAlign.Right
            }
            editor.addOpenListener { event ->
              event.bean.produto?.let { produto ->
                val locSulfixos = produto.localizacoes().map { LocProduto(it) }
                comboLoc.setItems(locSulfixos)
                comboLoc.setItemCaptionGenerator { it.localizacao }
                comboLoc.value = event.bean.localizacao
              }
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
              if (it.saldoFinal < 0)
                "error_row"
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
                .filter {it.saldoFinal >= 0}
              val allItens = gridProdutos.dataProvider.getAll()
              val naoSelect = allItens.minus(itens)
              viewModel.confirmaProdutos(itens, CONFERIDA)
              viewModel.confirmaProdutos(naoSelect, ENT_LOJA)
              close()
            }
          }
        }
      }
    }
  }
}
