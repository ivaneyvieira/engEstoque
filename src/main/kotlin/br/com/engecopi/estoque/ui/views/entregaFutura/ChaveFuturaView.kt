package br.com.engecopi.estoque.ui.views.entregaFutura

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.ui.print.PrintUtil.imprimeNotaConcluida
import br.com.engecopi.estoque.ui.print.PrintUtil.printText
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.entregaFutura.*
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.localDate
import com.github.mvysny.karibudsl.v8.*
import com.github.mvysny.karibudsl.v8.VAlign.Right
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons.CHECK
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.*
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

@AutoView("chave_futura")
class ChaveFuturaView : CrudLayoutView<ChaveFuturaVo, ChaveFuturaViewModel>(false), IChaveFuturaView {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = RegistryUserInfo.userDefaultIsAdmin

  init {
    viewModel = ChaveFuturaViewModel(this)
    layoutForm {
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px
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
    form("Chave Entrega Futura")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      updateOperationVisible = false
      addOperationVisible = false
      deleteOperationVisible = usuarioDefault.admin
      column(ChaveFuturaVo::numero) {
        caption = "Número NF"
        setSortProperty("numero")
      }
      column(ChaveFuturaVo::numeroBaixa) {
        caption = "NF Baixa"
        setSortProperty("numero")
      }
      grid.addComponentColumn { item ->
        Button().apply { //print {viewModel.imprimir(item)}.extend(this)
          val impresso = item?.impresso ?: true
          this.isEnabled = impresso == false || isAdmin
          this.icon = PRINT
          this.addClickListener { click ->
            val text = viewModel.imprimir(item?.entityVo?.nota)
            printText(impressora(), text)
            val print = item?.impresso ?: true
            click.button.isEnabled = print == false || isAdmin
            refreshGrid()
          }
        }
      }.id = "btnPrint"
      column(ChaveFuturaVo::loja) {
        caption = "Loja NF"
        setRenderer({ loja ->
          loja?.sigla ?: ""
        }, TextRenderer())
      }
      column(ChaveFuturaVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({ tipo ->
          tipo?.descricao ?: ""
        }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(ChaveFuturaVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(ChaveFuturaVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }

      column(ChaveFuturaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(ChaveFuturaVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(ChaveFuturaVo::usuario) {
        caption = "Usuário"
        setRenderer({
          it?.loginName ?: ""
        }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(ChaveFuturaVo::rota) {
        caption = "Rota"
      }
      column(ChaveFuturaVo::cliente) {
        caption = "Cliente"
        setSortProperty("cliente")
      }
    }
  }

  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }

  private fun impressora(): Printer {
    val impressora = usuarioDefault.impressoraSaci().trim()
    return Printer(if (impressora == "") "ENTREGA" else impressora)
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Chave da Nota Fiscal") { key ->
      val notaSaida = viewModel.findNotaSaidaKey(key)

      if (notaSaida.isNotEmpty()) {
        val dialog = DlgFuturaLoc(notaSaida, viewModel) { itens ->
          val nota = viewModel.processaKey(itens)
          val text = viewModel.imprimir(nota)
          imprimeNotaConcluida(nota)
          printText(impressora(), text)
          updateView()
        }
        dialog.showDialog()
      }
    }
  }

  private fun btnImprimeTudo(): Button {
    return Button("Imprime Etiquetas").apply {
      icon = PRINT
      addClickListener {
        val text = viewModel.imprimeTudo()
        printText(impressora(), text) //grid.refreshGrid()
      }
    }
  }
}

class DlgFutura(val localizacaoNota: LocalizacaoFutura, val viewModel: ChaveFuturaViewModel, val update: () -> Unit) :
  Window("Itens da Nota") {
  private lateinit var gridProdutos: Grid<ItemChaveFutura>

  init {
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px

      grupo("Expedição ${localizacaoNota.abreviacao}") {
        row {
          horizontalLayout {
            alignment = Alignment.BOTTOM_LEFT
            button("Confirma") {
              alignment = Alignment.BOTTOM_RIGHT
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                val produtosPepetidos = gridProdutos.selectedItems.filter {
                  val notaItemSaci = it.notaProdutoSaci
                  notaItemSaci.gradeGenerica
                }.groupBy { it.prdno }.filter { entry ->
                  entry.value.size > 1
                }
                if (produtosPepetidos.isNotEmpty()) {
                  viewModel.view.showWarning("Foi selecionado mais uma grade do mesmo produto")
                } else {
                  localizacaoNota.itensChaveFutura.forEach {
                    it.selecionado = false
                  }
                  val itensSelecionado = gridProdutos.selectedItems.toList().filter { !it.isSave() }

                  itensSelecionado.forEach {
                    it.selecionado = true
                  }
                  update()
                  close()
                }
              }
            }
            button("Cancela") {
              alignment = Alignment.BOTTOM_LEFT
              addClickListener {
                close()
              }
            }
          }
        }
        row {
          gridProdutos = grid(ItemChaveFutura::class) {
            val itens = localizacaoNota.itensChaveFutura

            this.dataProvider = ListDataProvider(itens)
            removeAllColumns()
            val selectionModel = setSelectionMode(MULTI)
            selectionModel.addSelectionListener { select ->
              if (select.isUserOriginated) {
                select.allSelectedItems.forEach {
                  if (it.isSave()) {
                    Notification.show("Não pode ser selecionado. Já está salvo")
                    selectionModel.deselect(it)
                  } else if (it.saldoFinal < -100000000) { //TODO Saldo insuficiente
                    Notification.show("Não pode ser selecionado. Saldo insuficiente.")
                    selectionModel.deselect(it)
                  }
                }
              }
            }

            setSizeFull()

            addColumnFor(ItemChaveFutura::prdno) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ItemChaveFutura::nome) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ItemChaveFutura::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ItemChaveFutura::saldo) {
              expandRatio = 1
              caption = "Saldo"
              align = Right
            }
            addColumnFor(ItemChaveFutura::quant) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = Right
            }
            addColumnFor(ItemChaveFutura::saldoFinal) {
              expandRatio = 1
              caption = "Saldo Final"
              align = Right
            }

            this.setStyleGenerator {
              when {
                it.isSave() -> "ok"
                it.saldoFinal < 0 -> "error_row"
                else -> null
              }
            }
          }
          localizacaoNota.itensChaveFutura.forEach { item ->
            if (item.selecionado) gridProdutos.select(item)
            else gridProdutos.deselect(item)
          }
        }
      }
    }
  }
}

class DlgFuturaLoc(
  val notaProdutoSaida: List<NotaProdutoSaci>,
  val viewModel: ChaveFuturaViewModel,
  val execConfirma: (itens: List<ItemChaveFutura>) -> Unit
) : Window("Localizações") {
  private lateinit var gridProdutos: Grid<LocalizacaoFutura>

  init {
    val nota = notaProdutoSaida.firstOrNull()
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px

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
              value = viewModel.findLoja(nota?.storeno)?.sigla
            }
            textField("Tipo") {
              expandRatio = 2f
              isReadOnly = true
              value = TipoNota.value(nota?.tipo)?.descricao
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
                val itens = gridProdutos.dataProvider.getAll().flatMap { loc ->
                  loc.itensChaveFutura.filter { it.selecionado }
                }
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
          gridProdutos = grid(LocalizacaoFutura::class) {
            val itens = notaProdutoSaida
            val abreviacaoItens = itens.groupBy { item ->
              val abreviacao = viewModel.abreviacoes(item.prdno, item.grade).sorted()
              abreviacao
            }
            val abreviacoes = abreviacaoItens.keys.flatten().distinct().map { abrev ->
              val itensVendaFutura =
                abreviacaoItens.filter { it.key.contains(abrev) }.map { it.value }.flatten().distinct()
                  .map { notaSaci ->
                    val saldo = viewModel.saldoProduto(notaSaci, abrev)
                    ItemChaveFutura(notaSaci, saldo, abrev)
                  }
              LocalizacaoFutura(abrev, itensVendaFutura)
            }.sortedBy { it.abreviacao }

            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()

            setSizeFull()
            addComponentColumn { item ->
              Button().apply {
                this.icon = CHECK
                this.addClickListener {
                  val dlg = DlgFutura(item, viewModel) {
                    gridProdutos.refresh()
                  }
                  dlg.showDialog()
                }
              }
            }.id = "btnPrintItens"
            addColumnFor(LocalizacaoFutura::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(LocalizacaoFutura::countSelecionado) {
              caption = "Selecionados"
              align = Right
            }
          }
        }
      }
    }
  }
}