package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.impressoraUsuario
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.ui.print.PrintUtil.printText
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo
import br.com.engecopi.estoque.viewmodel.movimentacao.ProdutoNotaVo
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.Binder
import com.vaadin.event.ShortcutAction
import com.vaadin.event.ShortcutAction.KeyCode.ENTER
import com.vaadin.icons.VaadinIcons
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.shared.ui.datefield.DateResolution
import com.vaadin.ui.*
import com.vaadin.ui.Grid.SelectionMode.MULTI
import org.vaadin.patrik.FastNavigation

abstract class NotaView<VO : NotaVo, MODEL : NotaViewModel<VO, V>, V : INotaView>(customFooterLayout: Boolean) :
        CrudLayoutView<VO, MODEL>(customFooterLayout) {
  lateinit var gridProduto: Grid<ProdutoNotaVo>
  val usuario get() = usuarioDefault
  val isAdmin get() = usuario.admin

  inline fun <reified V : NotaVo> (@VaadinDsl HasComponents).notaFiscalField(
    operation: CrudOperation?,
    binder: Binder<V>,
                                                                            ): TextField {
    return textField("Nota Fiscal") {
      expandRatio = 2f
      isReadOnly = operation != ADD
      bind(binder).bind(NotaVo::numeroNF.name)
      reloadBinderOnChange(binder)
    }
  }

  fun btnImprimeTudo(): Button {
    return Button("Imprime Etiquetas").apply {
      icon = PRINT
      addClickListener {
        printText(impressoraUsuario, viewModel.imprimirItensPendentes()) //grid.refreshGrid()
      }
    }
  }

  fun btnDesfazer(): Button {
    return Button("Cancelar").apply {
      this.isVisible = usuario.admin
      icon = VaadinIcons.CLOSE
      addClickListener {
        val itens = grid.selectedItems.firstOrNull()
        if (itens == null) showError("Não há item selecionado")
        else viewModel.desfazOperacao(itens.entityVo)
      }
    }
  }

  inline fun <reified V : NotaVo> (@VaadinDsl HasComponents).lojaField(operation: CrudOperation?, binder: Binder<V>) {
    comboBox<Loja>("Loja") {
      expandRatio = 2f
      default { it.sigla }
      isReadOnly = operation != ADD
      setItems(viewModel.findLojas(lojaDeposito))

      bind(binder).asRequired("A lojaDefault deve ser informada").bind(NotaVo::lojaNF.name)
      reloadBinderOnChange(binder)
    }
  }

  inline fun <reified V : NotaVo> VerticalLayout.produtoField(
    operation: CrudOperation?,
    binder: Binder<V>,
    tipo: String,
                                                             ) {
    row {
      this.bindVisible(binder, NotaVo::naoTemGrid.name)
      var dataValidade: DateField? = null
      comboBox<Produto>("Código") {
        expandRatio = 2f
        isReadOnly = operation != ADD
        default { "${it.codigo.trim()} ${it.grade}".trim() }
        isTextInputAllowed = true
        bindItens(binder, NotaVo::produtoNota.name)
        bind(binder).bind(NotaVo::produto.name)
        reloadBinderOnChange(binder)
        this.addValueChangeListener {
          it.value?.let { produto ->
            val meses = produto.mesesVencimento ?: 0
            dataValidade?.isVisible = meses > 0 && tipo == "Entrada"
          }
        }
      }
      textField("Descrição") {
        expandRatio = 5f
        isReadOnly = true
        bind(binder).bind(NotaVo::descricaoProduto.name)
      }
      dataValidade = dateField("Fabricação") {
        expandRatio = 1f
        val meses = binder.bean.produto?.mesesVencimento ?: 0
        this.isVisible = meses > 0 && tipo == "Entrada"
        isReadOnly = operation != ADD || isAdmin
        placeholder = "mm/aaaa"
        this.dateFormat = "MM/yyyy"
        this.resolution = DateResolution.MONTH
        bind(binder).bind(NotaVo::dataFabricacao.name)
      }
      comboBox<LocProduto>("Localização") {
        expandRatio = 2f
        isReadOnly = operation != ADD
        default { localizacao ->
          localizacao.localizacao
        }
        isTextInputAllowed = true

        bindItens(binder, NotaVo::localizacaoProduto.name)
        bind(binder).bind(NotaVo::localizacao.name)
      }
      textField("Grade") {
        expandRatio = 1f
        isReadOnly = true
        bind(binder).bind(NotaVo::grade.name)
      }
      integerField("Qtd $tipo") {
        expandRatio = 1f
        isReadOnly = (!this@NotaView.isAdmin) && (operation != ADD)
        this.bind(binder).bind(NotaVo::quantProduto.name)
      }
    }
    row {
      this.bindVisible(binder, NotaVo::temGrid.name)
      gridProduto = grid(ProdutoNotaVo::class) {
        expandRatio = 2f
        this.h = 200.px
        this.editor.isEnabled = true
        this.editor.binder

        removeAllColumns()
        val selectionModel = setSelectionMode(MULTI)
        selectionModel.addSelectionListener { select ->
          if (select.isUserOriginated) {
            this.dataProvider.getAll().forEach {
              it.selecionado = false
            }

            select.allSelectedItems.forEach {
              if (it.isSave) {
                it.selecionado = false
                selectionModel.deselect(it)
              }
              else it.selecionado = true
            }
          }
        }
        val comboLoc = ComboBox<LocProduto>().apply {
          isEmptySelectionAllowed = false
          isTextInputAllowed = false
        }

        val edtDataValidade = DateField().apply {
          placeholder = "mm/aaaa"
          this.dateFormat = "MM/yyyy"
          this.resolution = DateResolution.MONTH
        }

        addColumnFor(ProdutoNotaVo::codigo) {
          expandRatio = 1
          caption = "Código"
        }
        addColumnFor(ProdutoNotaVo::descricaoProduto) {
          expandRatio = 5
          caption = "Descrição"
        }
        addColumnFor(ProdutoNotaVo::dataFabricacaoStr) {
          expandRatio = 1
          caption = "Fabricação"
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
          caption = "Saldo Atual"
          align = VAlign.Right
        }
        addColumnFor(ProdutoNotaVo::quantidade) {
          expandRatio = 1
          caption = "Qtd $tipo"
          align = VAlign.Right
        }
        addColumnFor(ProdutoNotaVo::saldoFinal) {
          expandRatio = 1
          caption = "Saldo Final"
          align = VAlign.Right
        }
        bindItens(binder, NotaVo::produtos.name)
        editor.addOpenListener { event ->
          event.bean.produto.let { produto ->
            val locSulfixos = produto.localizacoes(abreviacaoDefault).map { LocProduto(it) }
            comboLoc.setItems(locSulfixos)
            comboLoc.setItemCaptionGenerator { it.localizacao }
            comboLoc.value = event.bean.localizacao
          }
        }
        val nav = FastNavigation(this, false, true)
        nav.changeColumnAfterLastRow = true
        nav.openEditorWithSingleClick = true
        nav.allowArrowToChangeRow = true
        nav.openEditorOnTyping = true
        nav.addEditorSaveShortcut(ENTER)
        editor.cancelCaption = "Cancelar"
        editor.saveCaption = "Salvar"
        editor.isBuffered = false
        this.setStyleGenerator {
          when {
            it.saldoFinal < 0 -> "error_row"
            it.isSave         -> "ok"
            else              -> null
          }
        }
      }
    }
  }
}

fun TextField.blockCLipboard() {
  this.addGlobalShortcutListener(KeyShortcut(ShortcutAction.KeyCode.V, setOf(ModifierKey.Ctrl))) {
    this.value = ""
  }
  this.addContextClickListener {
    this.value = ""
  }
}


