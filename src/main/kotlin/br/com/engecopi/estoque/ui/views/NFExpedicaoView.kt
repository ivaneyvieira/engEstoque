package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.NFExpedicaoViewModel
import br.com.engecopi.estoque.viewmodel.NFExpedicaoVo
import br.com.engecopi.estoque.viewmodel.ProdutoVO
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.utils.localDate
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.VAlign
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
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
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Alignment
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
import org.vaadin.viritin.fields.IntegerField

@AutoView("nf_expedicao")
class NFExpedicaoView: CrudLayoutView<NFExpedicaoVo, NFExpedicaoViewModel>() {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = RegistryUserInfo.userDefaultIsAdmin

  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }

  init {
    viewModel = NFExpedicaoViewModel(this)
    layoutForm {
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px
        val nota = binder.bean
        grupo("Nota fiscal de saída") {
          verticalLayout {
            row {
              textField("Nota Fiscal") {
                expandRatio = 2f
                isReadOnly = true
                value = nota.numero
              }
              textField("Loja") {
                expandRatio = 2f
                isReadOnly = true
                value = nota.loja?.sigla
              }
              textField("Tipo") {
                expandRatio = 2f
                isReadOnly = true
                value = nota.tipoNota?.descricao
              }
              dateField("Data") {
                expandRatio = 1f
                isReadOnly = true
                value = nota.data
              }
              textField("Rota") {
                expandRatio = 1f
                isReadOnly = true
                value = nota.rota
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expandRatio = 1f
                isReadOnly = true
                value = nota.observacao
              }
            }
          }
        }
      }
    }
    form("Nota Fiscal (Expedição)")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      updateOperationVisible = false
      addOperationVisible = false
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(NFExpedicaoVo::numero) {
        caption = "Número NF"
        setSortProperty("numero")
      }
      grid.addComponentColumn {item ->
        Button().apply {
          //print {viewModel.imprimir(item)}.extend(this)
          val impresso = item?.impresso ?: true
          this.isEnabled = impresso == false || isAdmin
          this.icon = VaadinIcons.PRINT
          this.addClickListener {
            openText(viewModel.imprimir(item?.entityVo?.nota))
            val print = item?.impresso ?: true
            it.button.isEnabled = print == false || isAdmin
            refreshGrid()
          }
        }
      }
        .id = "btnPrint"
      column(NFExpedicaoVo::loja) {
        caption = "Loja NF"
        setRenderer({loja ->
                      loja?.sigla ?: ""
                    }, TextRenderer())
      }
      column(NFExpedicaoVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({tipo ->
                      tipo?.descricao ?: ""
                    }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(NFExpedicaoVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(NFExpedicaoVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }

      column(NFExpedicaoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(NFExpedicaoVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(NFExpedicaoVo::usuario) {
        caption = "Usuário"
        setRenderer({
                      it?.loginName ?: ""
                    }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(NFExpedicaoVo::rota) {
        caption = "Rota"
      }
      column(NFExpedicaoVo::cliente) {
        caption = "Cliente"
        setSortProperty("cliente")
      }
    }
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Chave da Nota Fiscal") {key ->
      val notaSaida = viewModel.findNotaSaidaKey(key)
      if(notaSaida.isNotEmpty()) {
        val dialog = DlgNotaLoc(notaSaida, viewModel) {itens ->
          val abreviacoes = itens.map {it.localizacao}
          val nota = viewModel.processaKey(key, abreviacoes)
          openText(viewModel.imprimir(nota))
        }
        dialog.showDialog()
      }
    }
  }

  private fun btnImprimeTudo(): Button {
    return Button("Imprime Etiquetas").apply {
      icon = PRINT
      addClickListener {
        openText(viewModel.imprimeTudo())
        //grid.refreshGrid()
      }
    }
  }
}

class DlgNotaLoc(val notaSaida: List<NotaSaci>,
                 val viewModel: NFExpedicaoViewModel,
                 val execConfirma: (itens: List<NotaSaci>) -> Unit): Window("Nota de Saída") {
  private lateinit var gridProdutos: Grid<LocalizacaoNota>

  init {
    val nota = notaSaida.firstOrNull()
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
        .px

      grupo("Nota fiscal de saída") {
        verticalLayout {
          row {
            textField("Nota Fiscal") {
              expandRatio = 2f
              isReadOnly = true
              value = nota?.numero
            }
            textField("Loja") {
              expandRatio = 2f
              isReadOnly = true
              value = viewModel.findLoja(nota?.storeno)
                ?.sigla
            }
            textField("Tipo") {
              expandRatio = 2f
              isReadOnly = true
              value = TipoNota.value(nota?.tipo)
                ?.descricao
            }
            dateField("Data") {
              expandRatio = 1f
              isReadOnly = true
              value = nota?.date?.localDate()
            }
            textField("Rota") {
              expandRatio = 1f
              isReadOnly = true
              value = nota?.rota
            }
          }
        }
      }

      grupo("Localizações") {
        row {
          horizontalLayout {
            button("Confirma") {
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                val itens = gridProdutos.selectedItems.toList()
                execConfirma(itens)
                close()
              }
            }
            button("Cancela") {
              alignment = Alignment.BOTTOM_RIGHT
              addClickListener {
                close()
              }
            }
          }
        }
        row {
          gridProdutos = grid(LocalizacaoNota::class) {
            val itens = notaSaida
            val abreviacaoItens = itens.groupBy {item ->
              val abreviacao = viewModel.abreviacoes(item.prdno, item.grade)
              abreviacao
            }
            val abreviacoes = abreviacaoItens.map {entry ->
              LocalizacaoNota(entry.key, entry.value)
            }
              .toList()
              .sortedBy {it.abreviacao}

            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()
            //setSelectionMode(MULTI)
            setSizeFull()
            addColumnFor(LocalizacaoNota::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }

            addComponentColumn {item ->
              Button().apply {
                this.icon = PRINT
                this.addClickListener {}
              }
            }.id = "btnPrintItens"
          }
        }
      }
    }
  }
}

data class LocalizacaoNota(var abreviacao: String, val notaSaci: List<NotaSaci>)

class DlgNotaExpedicao(val localizacaoNota: LocalizacaoNota,
                       val viewModel: NFExpedicaoViewModel): Window("Itens da expedição") {
  private lateinit var gridProdutos: Grid<NotaSaci>

  init {
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
        .px

      grupo("Expedição ${localizacaoNota.abreviacao}") {
        row {
          gridProdutos = grid(NotaSaci::class) {
            val abreviacao = RegistryUserInfo.abreviacaoDefault
            //nota.refresh()
            val itens = localizacaoNota.notaSaci

            this.dataProvider = ListDataProvider(itens)
            removeAllColumns()
            val selectionModel = setSelectionMode(MULTI)
            selectionModel.addSelectionListener {select ->
              if(select.isUserOriginated) {
                select.allSelectedItems.forEach {
                  if(it.isSave()) {
                    Notification.show("Não pode ser selecionado")
                    selectionModel.deselect(it)
                  }
                }
              }
            }

            setSizeFull()

            addColumnFor(NotaSaci::prdno) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(NotaSaci::nome) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(NotaSaci::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(NotaSaci::quant) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = VAlign.Right
            }

            this.setStyleGenerator {
              if(!it.isSave()) "ok"
              else null
            }
          }
        }
      }

      row {
        horizontalLayout {
          alignment = Alignment.BOTTOM_RIGHT
          button("Cancela") {
            alignment = Alignment.BOTTOM_RIGHT
            addClickListener {
              close()
            }
          }
          button("Confirma") {
            alignment = Alignment.BOTTOM_RIGHT
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
