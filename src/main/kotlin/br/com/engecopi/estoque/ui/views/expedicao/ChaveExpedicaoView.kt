package br.com.engecopi.estoque.ui.views.expedicao

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.impressoraLocalizacao
import br.com.engecopi.estoque.model.RegistryUserInfo.userDefaultIsAdmin
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.print.PrintUtil.imprimeNotaConcluida
import br.com.engecopi.estoque.ui.print.PrintUtil.printText
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.expedicao.*
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.localDate
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.*
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

@AutoView("chave_expedicao")
class ChaveExpedicaoView : CrudLayoutView<ChaveExpedicaoVo, ChaveExpedicaoViewModel>(false), IChaveExpedicaoView {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = userDefaultIsAdmin

  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }

  init {
    viewModel = ChaveExpedicaoViewModel(this)
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
    form("Chave Expedição")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      updateOperationVisible = false
      addOperationVisible = false
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(ChaveExpedicaoVo::numero) {
        caption = "Número NF"
        setSortProperty("numero")
      }
      grid.addComponentColumn { item ->
        Button().apply { //print {viewModel.imprimir(item)}.extend(this)
          val impresso = item?.impresso ?: true
          this.isEnabled = impresso == false || isAdmin
          this.icon = PRINT
          this.addClickListener { click ->
            val pacotes = viewModel.imprimir(item?.entityVo?.nota)
            pacotes.forEach {
              printText(it.impressora, it.text)
            }
            val print = item?.impresso ?: true
            click.button.isEnabled = print == false || isAdmin
            refreshGrid()
          }
        }
      }.id = "btnPrint"
      column(ChaveExpedicaoVo::loja) {
        caption = "Loja NF"
        setRenderer({ loja ->
          loja?.sigla ?: ""
        }, TextRenderer())
      }
      column(ChaveExpedicaoVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({ tipo ->
          tipo?.descricao ?: ""
        }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(ChaveExpedicaoVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(ChaveExpedicaoVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(ChaveExpedicaoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(ChaveExpedicaoVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(ChaveExpedicaoVo::usuario) {
        caption = "Usuário"
        setRenderer({
          it?.loginName ?: ""
        }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(ChaveExpedicaoVo::rota) {
        caption = "Rota"
      }
      column(ChaveExpedicaoVo::cliente) {
        caption = "Cliente"
        setSortProperty("cliente")
      }
    }
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Chave da Nota Fiscal") { key ->
      val notaSaida = viewModel.findNotaSaidaKey(key)
      if (notaSaida.isNotEmpty()) {
        val dialog = DlgExpedicaoLoc(notaSaida, viewModel) { itens ->
          val nota = viewModel.processaKey(itens)
          val pacotes = viewModel.imprimir(nota)
          pacotes.forEach {
            printText(it.impressora, it.text)
          }
          imprimeNotaConcluida(nota)
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
        printText(impressoraLocalizacao, text) //grid.refreshGrid()
      }
    }
  }
}

class DlgExpedicaoLoc(
  val notaProdutoSaida: List<NotaProdutoSaci>,
  val viewModel: ChaveExpedicaoViewModel,
  val execConfirma: (itens: List<ItemExpedicao>) -> Unit
) : Window("Localizações") {
  private lateinit var gridProdutos: Grid<LocalizacaoExpedicao>

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
                  loc.itensExpedicao.filter { it.selecionado }
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
          gridProdutos = grid(LocalizacaoExpedicao::class) {
            val itens = notaProdutoSaida
            val abreviacaoItens = itens.groupBy { item ->
              val abreviacao = viewModel.abreviacoes(item.prdno, item.grade).sorted()
              abreviacao
            }
            val abreviacoes = abreviacaoItens.keys.asSequence().flatten().distinct().map { abrev ->
              val itensExpedicao =
                abreviacaoItens.filter { it.key.contains(abrev) }.map { it.value }.flatten().distinct()
                  .map { notaSaci ->
                    val saldo = viewModel.saldoProduto(notaSaci, abrev)
                    ItemExpedicao(notaSaci, saldo, abrev)
                  }
              LocalizacaoExpedicao(abrev, itensExpedicao)
            }.toList().sortedBy { it.abreviacao }.toList()

            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()

            setSizeFull()
            addComponentColumn { item ->
              Button().apply {
                this.icon = VaadinIcons.CHECK
                this.addClickListener {
                  val dlg = DlgExpedicao(item, viewModel) {
                    gridProdutos.refresh()
                  }
                  dlg.showDialog()
                }
              }
            }.id = "btnPrintItens"
            addColumnFor(LocalizacaoExpedicao::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(LocalizacaoExpedicao::countSelecionado) {
              caption = "Selecionados"
              align = VAlign.Right
            }
          }
        }
      }
    }
  }
}

class DlgExpedicao(
  val localizacaoExpedicao: LocalizacaoExpedicao, val viewModel: ChaveExpedicaoViewModel, val update: () -> Unit
) : Window("Itens da Nota") {
  private lateinit var gridProdutos: Grid<ItemExpedicao>

  init {
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px

      grupo("Expedição ${localizacaoExpedicao.abreviacao}") {
        row {
          horizontalLayout {
            alignment = Alignment.BOTTOM_LEFT
            button("Confirma") {
              alignment = Alignment.BOTTOM_RIGHT
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                localizacaoExpedicao.itensExpedicao.forEach {
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
            button("Cancela") {
              alignment = Alignment.BOTTOM_LEFT
              addClickListener {
                close()
              }
            }
          }
        }
        row {
          gridProdutos = grid(ItemExpedicao::class) {
            val itens = localizacaoExpedicao.itensExpedicao

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

            addColumnFor(ItemExpedicao::prdno) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ItemExpedicao::nome) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ItemExpedicao::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ItemExpedicao::saldo) {
              expandRatio = 1
              caption = "Saldo"
              align = VAlign.Right
            }
            addColumnFor(ItemExpedicao::quant) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = VAlign.Right
            }
            addColumnFor(ItemExpedicao::saldoFinal) {
              expandRatio = 1
              caption = "Saldo Final"
              align = VAlign.Right
            }

            this.setStyleGenerator {
              when {
                it.isSave() -> "ok"
                it.saldoFinal < 0 -> "error_row"
                else -> null
              }
            }
          }
          localizacaoExpedicao.itensExpedicao.forEach { item ->
            if (item.selecionado) gridProdutos.select(item)
            else gridProdutos.deselect(item)
          }
        }
      }
    }
  }
}
