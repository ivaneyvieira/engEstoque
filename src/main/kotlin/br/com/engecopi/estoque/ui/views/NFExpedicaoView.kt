package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.impressora
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.NFExpedicaoViewModel
import br.com.engecopi.estoque.viewmodel.NFExpedicaoVo
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
import com.github.mvysny.karibudsl.v8.refresh
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.Window
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme

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
          this.icon = PRINT
          this.addClickListener {
            val impressoa = item.abreviacao
            val text = viewModel.imprimir(item?.entityVo?.nota)
            printText(impressora, text)
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
          val nota = viewModel.processaKey(itens)
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
                val itens = gridProdutos.dataProvider.getAll()
                  .flatMap {loc ->
                    loc.itensExpedicao.filter {it.selecionado}
                      .map {item -> item.notaSaci}
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
          gridProdutos = grid(LocalizacaoNota::class) {
            val itens = notaSaida
            val abreviacaoItens = itens.groupBy {item ->
              val abreviacao = viewModel.abreviacoes(item.prdno, item.grade)
                .sorted()
              abreviacao
            }
            val abreviacoes = abreviacaoItens.keys.asSequence()
              .flatten()
              .distinct()
              .map {abrev ->
                val itens = abreviacaoItens.filter {it.key.contains(abrev)}
                  .map {it.value}
                  .flatten()
                  .distinct()
                  .map {notaSaci ->
                    val saldo = viewModel.saldoProduto(notaSaci, abrev)
                    ItemExpedicao(notaSaci, saldo)
                  }
                LocalizacaoNota(abrev, itens)
              }
              .toList()
              .sortedBy {it.abreviacao}
              .toList()

            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()
            //setSelectionMode(MULTI)
            setSizeFull()
            addComponentColumn {item ->
              Button().apply {
                this.icon = VaadinIcons.CHECK
                this.addClickListener {
                  val dlg = DlgNotaExpedicao(item, viewModel) {
                    gridProdutos.refresh()
                  }
                  dlg.showDialog()
                }
              }
            }.id = "btnPrintItens"
            addColumnFor(LocalizacaoNota::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(LocalizacaoNota::countSelecionado) {
              caption = "Selecionados"
              align = VAlign.Right
            }
          }
        }
      }
    }
  }
}

data class LocalizacaoNota(val abreviacao: String, val itensExpedicao: List<ItemExpedicao>) {
  val countSelecionado
    get() = itensExpedicao.filter {it.selecionado || it.isSave()}.size
}

data class ItemExpedicao(val notaSaci: NotaSaci, val saldo: Int, var selecionado: Boolean = false) {
  val prdno = notaSaci.prdno
  val grade = notaSaci.grade
  val nome = notaSaci.nome
  val quant = notaSaci.quant ?: 0
  val saldoFinal = saldo - quant

  fun isSave() = notaSaci.isSave()
}

class DlgNotaExpedicao(val localizacaoNota: LocalizacaoNota,
                       val viewModel: NFExpedicaoViewModel,
                       val update: () -> Unit): Window("Itens da expedição") {
  private lateinit var gridProdutos: Grid<ItemExpedicao>

  init {
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
        .px

      grupo("Expedição ${localizacaoNota.abreviacao}") {
        row {
          horizontalLayout {
            alignment = Alignment.BOTTOM_LEFT
            button("Confirma") {
              alignment = Alignment.BOTTOM_RIGHT
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                localizacaoNota.itensExpedicao.forEach {
                  it.selecionado = false
                }
                val itensSelecionado = gridProdutos.selectedItems.toList()
                  .filter {!it.isSave()}

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
            val itens = localizacaoNota.itensExpedicao

            this.dataProvider = ListDataProvider(itens)
            removeAllColumns()
            val selectionModel = setSelectionMode(MULTI)
            selectionModel.addSelectionListener {select ->
              if(select.isUserOriginated) {
                select.allSelectedItems.forEach {
                  if(it.isSave()) {
                    Notification.show("Não pode ser selecionado. Já está salvo")
                    selectionModel.deselect(it)
                  }
                  else if(it.saldoFinal < 0) {
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
                it.isSave()       -> "ok"
                it.saldoFinal < 0 -> "error_row"
                else              -> null
              }
            }
          }
          localizacaoNota.itensExpedicao.forEach {item ->
            if(item.selecionado) gridProdutos.select(item)
            else gridProdutos.deselect(item)
          }
        }
      }
    }
  }
}
