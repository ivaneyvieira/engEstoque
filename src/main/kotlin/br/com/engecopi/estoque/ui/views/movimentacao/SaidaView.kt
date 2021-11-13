package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.model.*
import br.com.engecopi.estoque.model.LancamentoOrigem.*
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.impressoraUsuario
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.StatusNota.*
import br.com.engecopi.estoque.ui.print.PrintUtil.imprimeNotaConcluida
import br.com.engecopi.estoque.ui.print.PrintUtil.printText
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.movimentacao.ISaidaView
import br.com.engecopi.estoque.viewmodel.movimentacao.ProdutoNotaVo
import br.com.engecopi.estoque.viewmodel.movimentacao.SaidaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.SaidaVo
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import br.com.engecopi.saci.QuerySaci
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.provider.GridSortOrder
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutAction.KeyCode.F2
import com.vaadin.icons.VaadinIcons
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.shared.data.sort.SortDirection.ASCENDING
import com.vaadin.shared.data.sort.SortDirection.DESCENDING
import com.vaadin.shared.ui.ContentMode.HTML
import com.vaadin.shared.ui.ValueChangeMode.LAZY
import com.vaadin.shared.ui.window.WindowMode
import com.vaadin.ui.*
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import org.vaadin.grideditorcolumnfix.GridEditorColumnFix
import org.vaadin.patrik.FastNavigation
import org.vaadin.viritin.fields.IntegerField
import java.time.LocalDateTime
import javax.ws.rs.client.Entity.html

@AutoView("")
class SaidaView : NotaView<SaidaVo, SaidaViewModel, ISaidaView>(customFooterLayout = true), ISaidaView {
  private var formCodBar: PnlCodigoBarras? = null
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }

  init {
    viewModel = SaidaViewModel(this)
    layoutForm {
      if (operation == ADD) {
        binder.bean.lojaNF = lojaDeposito
        binder.bean.usuario = usuario
        operationButton?.isEnabled = false
      }
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px
        grupo("Nota fiscal de saída") {
          verticalLayout {
            row {
              notaFiscalField(operation, binder)
              lojaField(operation, binder)
              comboBox<TipoNota>("Tipo") {
                expandRatio = 2f
                default { it.descricao }
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
        row {
          label("<b>Produto</b>") {
            contentMode = HTML
          }

          addComponent(footerLayout)
          setComponentAlignment(footerLayout, Alignment.BOTTOM_LEFT)
          addComponentsAndExpand(Label(""))
        }
        grupo {
          produtoField(operation, binder, "Saída")
        }
      }
      if (!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Saída de produtos")
    gridCrud {
      addCustomToolBarComponent(btnDesfazer())
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      addOnly = !isAdmin
      grid.addComponentColumn { item ->
        Button().apply {
          isEnabled = isAdmin
          icon = VaadinIcons.PRINT
          addClickListener {
            item.itemNota?.recalculaSaldos()
            val numero = item.numeroNF
            showQuestion(msg = "Imprimir todos os itens da nota $numero?",
                         execYes = { imprimeNotaCompleta(item) },
                         execNo = { imprimeItem(item) })
          }
        }
      }.id = "btnPrint"
      column(SaidaVo::numeroCodigoReduzido) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(SaidaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(SaidaVo::numeroBaixa) {
        caption = "NF Baixa"
      }
      column(SaidaVo::dataBaixa) {
        caption = "Data Baixa"
        dateFormat()
      }
      column(SaidaVo::lancamento) {
        caption = "Data Lançamento"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(SaidaVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(SaidaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({ loja -> loja?.sigla ?: "" }, TextRenderer())
      }
      column(SaidaVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
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
        setRenderer({ it?.abreviacao }, TextRenderer())
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
      grid.setStyleGenerator { saida ->
        if (saida.status == CONFERIDA) "pendente"
        else null
      }
    }
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Código de barras") { key ->
      val nota = viewModel.findByKey(key)
      if (nota == null || nota.vazio) {
        val msgErro = nota?.msgErro ?: "A nota não foi encontrada"
        showError(msgErro.ifEmpty { "A nota não foi encontrada" })
      }
      else {
        val dlg = DlgNotaSaida(nota, viewModel) { itens ->
          if (itens.isNotEmpty()) {
            val text = viewModel.imprimirItens(itens)
            printText(impressoraUsuario, text)
            refreshGrid() //Imprime nota
            itens.firstOrNull()?.nota?.let { nota ->
              imprimeNotaConcluida(nota)
            }
          }
        }
        dlg.showDialog()
        Thread.sleep(1000)
        dlg.focusEditor()
      }
    }
  }

  private fun imprimeItem(item: SaidaVo) {
    val text = viewModel.imprimirItem(item.itemNota)
    printText(impressoraUsuario, text)
    refreshGrid()
  }

  private fun imprimeNotaCompleta(item: SaidaVo) {
    val text = viewModel.imprimirNotaCompleta(item.itemNota)
    printText(impressoraUsuario, text)
    refreshGrid()
  }
}

class DlgNotaSaida(
  val nota: NotaItens,
  val viewModel: SaidaViewModel,
  val execPrint: (List<ItemNota>) -> Unit,
                  ) : Window("Nota de Saída") {
  private lateinit var grupoSelecaoCol: Column<ProdutoNotaVo, Int>
  private lateinit var dateUpdateCol: Column<ProdutoNotaVo, LocalDateTime>
  private lateinit var gridProdutos: Grid<ProdutoNotaVo>
  private val edtBarcode = TextField()

  fun focusEditor() {
    edtBarcode.focus()
  }

  init {
    windowMode = WindowMode.MAXIMIZED

    addBlurListener {
      edtBarcode.focus()
    }

    verticalLayout {
      setSizeFull()
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
      grupo(expand = true) {
        row {
          horizontalLayout {
            button("Confirma") {
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                val allItens = gridProdutos.dataProvider.getAll()
                val itensGroupByOrigem = allItens.groupBy { it.value?.nota?.lancamentoOrigem }
                val itensSelecionados = gridProdutos.selectedItems.toList().filter { it.allowSelect() }
                val naoSelect = allItens.minus(itensSelecionados).filter { it.allowSelect() }

                itensGroupByOrigem.forEach { (origem, produtos) ->
                  if (origem != null) when (origem) {
                    DEPOSITO  -> confirmaProdutos(produtosSelecionado = produtos intersect itensSelecionados,
                                                  produtosNaoSelecionado = produtos intersect naoSelect,
                                                  statusSelecionado = ENTREGUE)
                    EXPEDICAO -> confirmaProdutos(produtosSelecionado = produtos intersect itensSelecionados,
                                                  produtosNaoSelecionado = produtos intersect naoSelect,
                                                  statusSelecionado = CONFERIDA)
                    ENTREGA_F -> confirmaProdutos(produtosSelecionado = produtos intersect itensSelecionados,
                                                  produtosNaoSelecionado = produtos intersect naoSelect,
                                                  statusSelecionado = CONFERIDA)
                    RESSUPRI  -> confirmaProdutos(produtosSelecionado = produtos intersect itensSelecionados,
                                                  produtosNaoSelecionado = produtos intersect naoSelect,
                                                  statusSelecionado = CONFERIDA)
                    ABASTECI  -> confirmaProdutos(produtosSelecionado = produtos intersect itensSelecionados,
                                                  produtosNaoSelecionado = emptySet(),
                                                  statusSelecionado = ENTREGUE)
                  }
                }
                close()
              }
            }
            button("Cancela") {
              addClickListener {
                close()
              }
            }
          }
          html("<b>Código de barras</b>")
          edtBarcode.apply {
            addValueChangeListener {
              val barcode = it.value
              execBarcode(barcode)
              gridProdutos.sortDefault()
            }
            this.addGlobalShortcutListener(F2) {
              focusEditor()
            }

            if (!QuerySaci.test) {
              this.valueChangeMode = LAZY
              valueChangeTimeout = 200
              this.blockCLipboard()
            }
          }
          this.addComponentsAndExpand(edtBarcode)
        }
        row(expand = true) {
          gridProdutos = grid(ProdutoNotaVo::class) {
            isExpanded = true
            GridEditorColumnFix(this)
            setSizeFull()
            this.tabIndex = -1
            updateProdutosNota()
            removeAllColumns()
            val selectionModel = setSelectionMode(MULTI)
            selectionModel.addSelectionListener { select ->
              if (select.isUserOriginated) {
                this.dataProvider.getAll().forEach {
                  it.selecionado = false
                  it.updateItem(false)
                }
                select.allSelectedItems.forEach {
                  if (it.saldoFinal < -100000000) { //TODO Saldo insuficiente
                    Notification.show("Saldo insuficiente")
                    selectionModel.deselect(it)
                    it.selecionado = false
                    it.updateItem(false)
                  }
                  else if (!it.allowSelect()) {
                    Notification.show("Não editavel")
                    selectionModel.deselect(it)
                    it.selecionado = false
                    it.updateItem(false)
                  }
                  else if (!RegistryUserInfo.userDefaultIsAdmin) {
                    Notification.show("Usuário não é administrador")
                    selectionModel.deselect(it)
                    it.selecionado = false
                    it.updateItem(false)
                  }
                  else {
                    it.selecionado = true
                    it.updateItem(false)
                  }
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
            dateUpdateCol = addColumnFor(ProdutoNotaVo::dateUpdate) {
              this.isHidden = true
            }
            grupoSelecaoCol = addColumnFor(ProdutoNotaVo::ordermSelecao) {
              this.isHidden = true
            }
            addColumnFor(ProdutoNotaVo::codigo) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ProdutoNotaVo::gtin) {
              expandRatio = 1
              caption = "Gtin"
            }
            addColumnFor(ProdutoNotaVo::descricaoProduto) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ProdutoNotaVo::localizacao) {
              expandRatio = 4
              caption = "Localização"
              setEditorComponent(comboLoc)
            }
            addColumnFor(ProdutoNotaVo::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ProdutoNotaVo::saldo) {
              expandRatio = 1
              caption = "Saldo"
              align = VAlign.Right
            }
            addColumnFor(ProdutoNotaVo::quantidade) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = VAlign.Right
              setEditorComponent(edtQuant)
            }
            addColumnFor(ProdutoNotaVo::saldoFinal) {
              expandRatio = 1
              caption = "Saldo Final"
              align = VAlign.Right
            }
            editor.addOpenListener { event ->
              event.bean.produto.let { produto ->
                val locSulfixos = produto.localizacoes(abreviacaoDefault).map { LocProduto(it) }
                comboLoc.setItems(locSulfixos)
                comboLoc.setItemCaptionGenerator { it.localizacao }
                comboLoc.value = event.bean.localizacao
              }
              comboLoc.isReadOnly = !event.bean.allowEdit()
              edtQuant.isReadOnly = !event.bean.allowEdit()
            }
            val nav = FastNavigation(this, false, true)
            nav.changeColumnAfterLastRow = true
            nav.openEditorWithSingleClick = true
            nav.allowArrowToChangeRow = true
            nav.openEditorOnTyping = true
            nav.addEditorSaveShortcut(KeyCode.ENTER)
            editor.cancelCaption = "Cancelar"
            editor.saveCaption = "Salvar"
            editor.isBuffered = false
            this.setStyleGenerator {
              if (it.saldoFinal < 0) "error_row"
              else if (!it.allowSelect()) "ok"
              else null
            }
            sortDefault()
          }
        }
      }
    }
  }

  private fun confirmaProdutos(
    produtosSelecionado: Set<ProdutoNotaVo>,
    produtosNaoSelecionado: Set<ProdutoNotaVo>,
    statusSelecionado: StatusNota,
                              ) {
    viewModel.confirmaProdutos(produtosSelecionado.toList(), statusSelecionado)
    viewModel.confirmaProdutos(produtosNaoSelecionado.toList(), ENT_LOJA)
  }

  private fun @VaadinDsl Grid<ProdutoNotaVo>.updateProdutosNota() {
    val abreviacao = abreviacaoDefault //nota.refresh()
    val itens = nota.itens.filter { it.localizacao.startsWith(abreviacao) }
    val itensProvider = itens.mapNotNull { item ->
      val produto = item.produto
      val statusNota = item.status
      val isSave = item.id != 0L
      val dataValidade = item.dataValidade
      if (produto != null) ProdutoNotaVo(produto = produto,
                                         statusNota = statusNota,
                                         dataValidade = dataValidade,
                                         localizacao = LocProduto(item.localizacao),
                                         isSave = isSave).apply {
        this.quantidade = item.quantidade
        this.value = item
        this.updateItem(false)
      }
      else null
    }.sortedByDescending { it.dateUpdate }

    this.dataProvider = ListDataProvider(itensProvider)
  }

  private fun Grid<ProdutoNotaVo>.sortDefault() {
    clearSortOrder()
    sortOrder = listOf(GridSortOrder(grupoSelecaoCol, ASCENDING), GridSortOrder(dateUpdateCol, DESCENDING))
  }

  private fun execBarcode2(barcode: String?) {
    if (!barcode.isNullOrBlank()) {
      val barcodeVolume = viewModel.findByBarcodeProduto2(barcode)
      if (barcodeVolume == null) {
        viewModel.view.showWarning("Produto não encontrado no saci")
      }
      else if(!barcodeVolume.isDataVencimentoValida()){
        viewModel.view.showWarning("Data de vencimento não é válida")
      }
      else {
        val produtosVO = gridProdutos.dataProvider.getAll()
        produtosVO.forEach { it.updateItem(false) }
        val produtos = produtosVO.mapNotNull { it.value?.produto }
        val interProdutos = produtos intersect listOf(barcodeVolume.produto)
        if (interProdutos.isEmpty()) {
          viewModel.view.showWarning("Produto não encontrado no grid")
        }
        else {
          interProdutos.forEach { produto ->
            val itemVO = produtosVO.filter { it.value?.produto?.id == produto.id }
            itemVO.forEach { item ->
              val codigo = item.value?.codigo?.trim()
              when { //TODO Saldo Negativo
                // item.saldoFinal < 0 -> viewModel.view.showWarning("O saldo final do produto '$codigo' está negativo")
                item.allowSelect() -> {
                  item.quantidade = item.quantidade + (barcodeVolume.quantidadeVolume ?: 0)
                  selecionaProduto(item)
                }
                else               -> viewModel.view.showWarning("O produto '$codigo' não é selecionavel")
              }
            }
          }
        }
      }
      edtBarcode.focus()
      edtBarcode.selectAll()
    }
  }

  private fun execBarcode(barcode: String?) {
    if(!barcode.isNullOrBlank()) {
      val listProduto = viewModel.findByBarcodeProduto(barcode)
      if(listProduto.isEmpty()) viewModel.view.showWarning("Produto não encontrado no saci")
      else {
        val produtosVO = gridProdutos.dataProvider.getAll()
        produtosVO.forEach {it.updateItem(false)}
        val produtos = produtosVO.mapNotNull {it.value?.produto}
        val interProdutos = produtos intersect listProduto
        interProdutos.forEach {produto ->
          val itemVO = produtosVO.filter {it.value?.produto?.id == produto.id}
          itemVO.forEach {item ->
            val codigo = item.value?.codigo?.trim()
            when {
              //TODO Saldo Negativo
              // item.saldoFinal < 0 -> viewModel.view.showWarning("O saldo final do produto '$codigo' está negativo")
              item.allowSelect() -> selecionaProduto(item)
              else               -> viewModel.view.showWarning("O produto '$codigo' não é selecionavel")
            }
          }
        }
        if(interProdutos.isEmpty()) viewModel.view.showWarning("Produto não encontrado no grid")
      }
      edtBarcode.focus()
      edtBarcode.selectAll()
    }
  }

  private fun selecionaProduto(item: ProdutoNotaVo) {
    gridProdutos.select(item)
    item.selecionado = true
    item.updateItem(true)
    when (item.value?.nota?.lancamentoOrigem) {
      DEPOSITO -> {
        execPrint(viewModel.confirmaProdutos(listOf(item), ENTREGUE))
        gridProdutos.updateProdutosNota()
      }
      else     -> {
        execPrint(viewModel.confirmaProdutos(listOf(item), CONFERIDA))
        gridProdutos.updateProdutosNota()
      }
    }
    if (gridProdutos.dataProvider.getAll().all { !it.allowSelect() }) close()
  }
}

