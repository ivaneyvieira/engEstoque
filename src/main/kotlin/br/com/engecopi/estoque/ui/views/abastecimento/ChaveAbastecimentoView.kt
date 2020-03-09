package br.com.engecopi.estoque.ui.views.abastecimento

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.impressoraUsuario
import br.com.engecopi.estoque.model.RegistryUserInfo.userDefaultIsAdmin
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.print.PrintUtil.imprimeNotaConcluida
import br.com.engecopi.estoque.ui.print.PrintUtil.printText
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.abastecimento.AbastecimentoVo
import br.com.engecopi.estoque.viewmodel.abastecimento.ChaveAbastecimentoViewModel
import br.com.engecopi.estoque.viewmodel.abastecimento.IAbastecimentoView
import br.com.engecopi.estoque.viewmodel.abastecimento.ItemAbastecimento
import br.com.engecopi.estoque.viewmodel.abastecimento.LocalizacaoAbestecimento
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import br.com.engecopi.saci.beans.NotaProdutoSaci
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

@AutoView("pedido_abastecimento")
class ChaveAbastecimentoView: CrudLayoutView<AbastecimentoVo, ChaveAbastecimentoViewModel>(),
                              IAbastecimentoView {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = userDefaultIsAdmin
  
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }
  
  init {
    viewModel = ChaveAbastecimentoViewModel(this)
    layoutForm {
      formLayout.apply {
        w =
          (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
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
      column(AbastecimentoVo::chave) {
        caption = "Chave"
        setSortProperty("numero")
      }
      grid.addComponentColumn {item ->
        Button().apply {
          //print {viewModel.imprimir(item)}.extend(this)
          val impresso = item?.impresso ?: true
          this.isEnabled = impresso == false || isAdmin
          this.icon = PRINT
          this.addClickListener {click ->
            val pacotes = viewModel.imprimir(item?.entityVo?.nota)
            pacotes.forEach {
              printText(it.impressora, it.text)
            }
            val print = item?.impresso ?: true
            click.button.isEnabled = print == false || isAdmin
            refreshGrid()
          }
        }
      }
        .id = "btnPrint"
      column(AbastecimentoVo::loja) {
        caption = "Loja NF"
        setRenderer({loja ->
                      loja?.sigla ?: ""
                    }, TextRenderer())
      }
      column(AbastecimentoVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({tipo ->
                      tipo?.descricao ?: ""
                    }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(AbastecimentoVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(AbastecimentoVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(AbastecimentoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(AbastecimentoVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(AbastecimentoVo::usuario) {
        caption = "Usuário"
        setRenderer({
                      it?.loginName ?: ""
                    }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(AbastecimentoVo::rota) {
        caption = "Rota"
      }
      column(AbastecimentoVo::cliente) {
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
        printText(impressoraUsuario, text)
        //grid.refreshGrid()
      }
    }
  }
}

class DlgNotaLoc(val notaProdutoSaida: List<NotaProdutoSaci>,
                 val viewModel: ChaveAbastecimentoViewModel,
                 val execConfirma: (itens: List<ItemAbastecimento>) -> Unit): Window("Nota de Saída") {
  private lateinit var gridProdutos: Grid<LocalizacaoAbestecimento>
  
  init {
    val nota = notaProdutoSaida.firstOrNull()
    verticalLayout {
      w =
        (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
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
              value =
                viewModel.findLoja(nota?.storeno)
                  ?.sigla
            }
            textField("Tipo") {
              expandRatio = 2f
              isReadOnly = true
              value =
                TipoNota.value(nota?.tipo)
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
                val itens =
                  gridProdutos.dataProvider.getAll()
                    .flatMap {loc ->
                      loc.itensAbastecimento.filter {it.selecionado}
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
          gridProdutos = grid(LocalizacaoAbestecimento::class) {
            val itens = notaProdutoSaida
            val abreviacaoItens = itens.groupBy {item ->
              val abreviacao =
                viewModel.abreviacoes(item.prdno, item.grade)
                  .sorted()
              abreviacao
            }
            val abreviacoes =
              abreviacaoItens.keys.asSequence()
                .flatten()
                .distinct()
                .map {abrev ->
                  val itensAbastecimento =
                    abreviacaoItens.filter {it.key.contains(abrev)}
                      .map {it.value}
                      .flatten()
                      .distinct()
                      .map {notaSaci ->
                        val saldo = viewModel.saldoProduto(notaSaci, abrev)
                        ItemAbastecimento(notaSaci, saldo, abrev)
                      }
                  LocalizacaoAbestecimento(abrev, itensAbastecimento)
                }
                .toList()
                .sortedBy {it.abreviacao}
                .toList()
            
            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()
            
            setSizeFull()
            addComponentColumn {item ->
              Button().apply {
                this.icon = VaadinIcons.CHECK
                this.addClickListener {
                  val dlg = DlgNotaAbastecimento(item, viewModel) {
                    gridProdutos.refresh()
                  }
                  dlg.showDialog()
                }
              }
            }.id = "btnPrintItens"
            addColumnFor(LocalizacaoAbestecimento::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(LocalizacaoAbestecimento::countSelecionado) {
              caption = "Selecionados"
              align = VAlign.Right
            }
          }
        }
      }
    }
  }
}

class DlgNotaAbastecimento(val localizacaoAbestecimento: LocalizacaoAbestecimento,
                           val viewModel: ChaveAbastecimentoViewModel,
                           val update: () -> Unit): Window("Itens da expedição") {
  private lateinit var gridProdutos: Grid<ItemAbastecimento>
  
  init {
    verticalLayout {
      w =
        (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px
      
      grupo("Expedição ${localizacaoAbestecimento.abreviacao}") {
        row {
          horizontalLayout {
            alignment = Alignment.BOTTOM_LEFT
            button("Confirma") {
              alignment = Alignment.BOTTOM_RIGHT
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                localizacaoAbestecimento.itensAbastecimento.forEach {
                  it.selecionado = false
                }
                val itensSelecionado =
                  gridProdutos.selectedItems.toList()
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
          gridProdutos = grid(ItemAbastecimento::class) {
            val itens = localizacaoAbestecimento.itensAbastecimento
            
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
            
            addColumnFor(ItemAbastecimento::prdno) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ItemAbastecimento::nome) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ItemAbastecimento::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ItemAbastecimento::saldo) {
              expandRatio = 1
              caption = "Saldo"
              align = VAlign.Right
            }
            addColumnFor(ItemAbastecimento::quant) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = VAlign.Right
            }
            addColumnFor(ItemAbastecimento::saldoFinal) {
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
          localizacaoAbestecimento.itensAbastecimento.forEach {item ->
            if(item.selecionado) gridProdutos.select(item)
            else gridProdutos.deselect(item)
          }
        }
      }
    }
  }
}
