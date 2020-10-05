package br.com.engecopi.estoque.ui.views.ressuprimento

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.print.PrintUtil
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.ressuprimento.ChaveRessuprimentoViewModel
import br.com.engecopi.estoque.viewmodel.ressuprimento.ChaveRessuprimentoVo
import br.com.engecopi.estoque.viewmodel.ressuprimento.IChaveRessuprimentoView
import br.com.engecopi.estoque.viewmodel.ressuprimento.ItemRessuprimento
import br.com.engecopi.estoque.viewmodel.ressuprimento.LocalizacaoRessuprimento
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.localDate
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.VAlign.Right
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
import com.vaadin.icons.VaadinIcons.CHECK
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

@AutoView("chave_ressuprimento")
class ChaveRessuprimentoView:
  CrudLayoutView<ChaveRessuprimentoVo, ChaveRessuprimentoViewModel>(), IChaveRessuprimentoView {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = RegistryUserInfo.userDefaultIsAdmin
  
  init {
    viewModel = ChaveRessuprimentoViewModel(this)
    layoutForm {
      formLayout.apply {
        w =
          (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
            .px
        val nota = binder.bean
        grupo("Pedido de ressuprimento") {
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
    form("Chave Ressuprimento")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      updateOperationVisible = false
      addOperationVisible = false
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(ChaveRessuprimentoVo::codigoBarraConferencia) {
        caption = "Chave NF"
        setSortProperty("codigoBarraConferencia")
      }
      column(ChaveRessuprimentoVo::numeroBaixa) {
        caption = "NF Baixa"
        setSortProperty("numero")
      }
      grid.addComponentColumn {item ->
        Button().apply {
          //print {viewModel.imprimir(item)}.extend(this)
          val impresso = item?.impresso ?: true
          this.isEnabled = impresso == false || isAdmin
          this.icon = PRINT
          this.addClickListener {click ->
            viewModel.imprimir(item?.entityVo?.nota)
              .forEach {printEtiqueta ->
                PrintUtil.printText(printEtiqueta.abreviacao.printer, printEtiqueta.text)
              }
            val print = item?.impresso ?: true
            click.button.isEnabled = print == false || isAdmin
            refreshGrid()
          }
        }
      }
        .id = "btnPrint"
      column(ChaveRessuprimentoVo::loja) {
        caption = "Loja NF"
        setRenderer({loja ->
                      loja?.sigla ?: ""
                    }, TextRenderer())
      }
      column(ChaveRessuprimentoVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({tipo ->
                      tipo?.descricao ?: ""
                    }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(ChaveRessuprimentoVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(ChaveRessuprimentoVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      
      column(ChaveRessuprimentoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(ChaveRessuprimentoVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(ChaveRessuprimentoVo::usuario) {
        caption = "Usuário"
        setRenderer({
                      it?.loginName ?: ""
                    }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(ChaveRessuprimentoVo::rota) {
        caption = "Rota"
      }
      column(ChaveRessuprimentoVo::cliente) {
        caption = "Cliente"
        setSortProperty("cliente")
      }
    }
  }
  
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }
  
  private fun btnImprimeTudo(): Button {
    return Button("Imprime Etiquetas").apply {
      icon = PRINT
      addClickListener {
        viewModel.imprimeTudo()
          .forEach {printEtiqueta ->
            PrintUtil.printText(printEtiqueta.abreviacao.printer, printEtiqueta.text)
          }
      }
    }
  }
  
  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Chave da Nota Fiscal") {key ->
      val notaSaida = viewModel.findNotaSaidaKey(key)
      
      if(notaSaida.isNotEmpty()) {
        val dialog = DlgRessuprimentoLoc(notaSaida, viewModel) {itens ->
          val nota = viewModel.processaKey(itens)
          val text = viewModel.imprimir(nota)
          PrintUtil.imprimeNotaConcluida(nota)
          viewModel.imprimir(nota)
            .forEach {printEtiqueta ->
              PrintUtil.printText(printEtiqueta.abreviacao.printer, printEtiqueta.text)
            }
          updateView()
        }
        dialog.showDialog()
      }
    }
  }
  
  override fun updateGrid() {
    grid.refresh()
  }
}

class DlgRessuprimento(val localizacaoNota: LocalizacaoRessuprimento,
                       val viewModel: ChaveRessuprimentoViewModel,
                       val update: () -> Unit): Window("Itens da Ressuprimento") {
  private lateinit var gridProdutos: Grid<ItemRessuprimento>
  
  init {
    verticalLayout {
      w =
        (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px
      
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
                }
                  .groupBy {it.prdno}
                  .filter {entry ->
                    entry.value.size > 1
                  }
                if(produtosPepetidos.isNotEmpty()) {
                  viewModel.view.showWarning("Foi selecionado mais uma grade do mesmo produto")
                }
                else {
                  localizacaoNota.itensRessuprimento.forEach {
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
          gridProdutos = grid(ItemRessuprimento::class) {
            val itens = localizacaoNota.itensRessuprimento
            
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
            
            addColumnFor(ItemRessuprimento::prdno) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ItemRessuprimento::nome) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ItemRessuprimento::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ItemRessuprimento::saldo) {
              expandRatio = 1
              caption = "Saldo"
              align = Right
            }
            addColumnFor(ItemRessuprimento::quant) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = Right
            }
            addColumnFor(ItemRessuprimento::saldoFinal) {
              expandRatio = 1
              caption = "Saldo Final"
              align = Right
            }
            
            this.setStyleGenerator {
              when {
                it.isSave()       -> "ok"
                it.saldoFinal < 0 -> "error_row"
                else              -> null
              }
            }
          }
          localizacaoNota.itensRessuprimento.forEach {item ->
            if(item.selecionado) gridProdutos.select(item)
            else gridProdutos.deselect(item)
          }
        }
      }
    }
  }
}

class DlgRessuprimentoLoc(val notaProdutoSaida: List<NotaProdutoSaci>,
                          val viewModel: ChaveRessuprimentoViewModel,
                          val execConfirma: (itens: List<ItemRessuprimento>) -> Unit): Window("Localizações") {
  private lateinit var gridProdutos: Grid<LocalizacaoRessuprimento>
  
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
                      loc.itensRessuprimento.filter {it.selecionado}
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
          gridProdutos = grid(LocalizacaoRessuprimento::class) {
            val itens = notaProdutoSaida
            val abreviacaoItens = itens.groupBy {item ->
              val abreviacao =
                viewModel.abreviacoes(item.prdno, item.grade)
                  .sorted()
              abreviacao
            }
            val abreviacoes =
              abreviacaoItens.keys.flatten()
                .distinct()
                .map {abrev ->
                  val itensRessuprimento =
                    abreviacaoItens.filter {it.key.contains(abrev)}
                      .map {it.value}
                      .flatten()
                      .distinct()
                      .map {notaSaci ->
                        val saldo = viewModel.saldoProduto(notaSaci, abrev)
                        ItemRessuprimento(notaSaci, saldo, abrev)
                      }
                  LocalizacaoRessuprimento(abrev, itensRessuprimento)
                }
                .sortedBy {it.abreviacao}
            
            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()
            
            setSizeFull()
            addComponentColumn {item ->
              Button().apply {
                this.icon = CHECK
                this.addClickListener {
                  val dlg = DlgRessuprimento(item, viewModel) {
                    gridProdutos.refresh()
                  }
                  dlg.showDialog()
                }
              }
            }.id = "btnPrintItens"
            addColumnFor(LocalizacaoRessuprimento::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(LocalizacaoRessuprimento::countSelecionado) {
              caption = "Selecionados"
              align = Right
            }
          }
        }
      }
    }
  }
}