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
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.CrudOperation
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.bindItens
import br.com.engecopi.framework.ui.view.bindVisible
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.integerField
import br.com.engecopi.framework.ui.view.reloadBinderOnChange
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.VAlign
import com.github.mvysny.karibudsl.v8.VaadinDsl
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.getAll
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.h
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.vaadin.data.Binder
import com.vaadin.event.ShortcutAction.KeyCode.ENTER
import com.vaadin.icons.VaadinIcons
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.ui.Button
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.HasComponents
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import org.vaadin.patrik.FastNavigation

abstract class NotaView<VO: NotaVo, MODEL: NotaViewModel<VO, V>, V: INotaView>: CrudLayoutView<VO, MODEL>() {
  lateinit var gridProduto: Grid<ProdutoNotaVo>
  val usuario get() = usuarioDefault
  val isAdmin get() = usuario.admin
  
  inline fun <reified V: NotaVo> (@VaadinDsl HasComponents).notaFiscalField(operation: CrudOperation?,
                                                                            binder: Binder<V>): TextField {
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
        printText(impressoraUsuario, viewModel.imprimirItensPendentes())
        //grid.refreshGrid()
      }
    }
  }
  
  fun btnDesfazer(): Button {
    return Button("Cancelar").apply {
      this.isVisible = usuario.admin
      icon = VaadinIcons.CLOSE
      addClickListener {
        val itens = grid.selectedItems.firstOrNull()
        if(itens == null) showError("Não há item selecionado")
        else viewModel.desfazOperacao(itens.entityVo)
      }
    }
  }
  
  inline fun <reified V: NotaVo> (@VaadinDsl HasComponents).lojaField(operation: CrudOperation?, binder: Binder<V>) {
    comboBox<Loja>("Loja") {
      expandRatio = 2f
      default {it.sigla}
      isReadOnly = operation != ADD
      setItems(viewModel.findLojas(lojaDeposito))
  
      bind(binder).asRequired("A lojaDefault deve ser informada")
        .bind(NotaVo::lojaNF.name)
      reloadBinderOnChange(binder)
    }
  }
  
  inline fun <reified V: NotaVo> VerticalLayout.produtoField(operation: CrudOperation?,
                                                             binder: Binder<V>,
                                                             tipo: String) {
    row {
      this.bindVisible(binder, NotaVo::naoTemGrid.name)
      comboBox<Produto>("Código") {
        expandRatio = 2f
        isReadOnly = operation != ADD
        default {"${it.codigo.trim()} ${it.grade}".trim()}
        isTextInputAllowed = true
        bindItens(binder, NotaVo::produtoNota.name)
        bind(binder).bind(NotaVo::produto.name)
        reloadBinderOnChange(binder)
      }
      textField("Descrição") {
        expandRatio = 5f
        isReadOnly = true
        bind(binder).bind(NotaVo::descricaoProduto.name)
      }
      comboBox<LocProduto>("Localizacao") {
        expandRatio = 3f
        isReadOnly = operation != ADD
        default {localizacao ->
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
        this.bind(binder)
          .bind(NotaVo::quantProduto.name)
      }
    }
    row {
      this.bindVisible(binder, NotaVo::temGrid.name)
      gridProduto = grid(ProdutoNotaVo::class) {
        expandRatio = 2f
        this.h = 200.px
        editor.isEnabled = true
        removeAllColumns()
        val selectionModel = setSelectionMode(MULTI)
        selectionModel.addSelectionListener {select ->
          if(select.isUserOriginated) {
            this.dataProvider.getAll()
              .forEach {
                it.selecionado = false
              }
  
            select.allSelectedItems.forEach {
              if(it.isSave) {
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
    
        addColumnFor(ProdutoNotaVo::codigo) {
          expandRatio = 1
          caption = "Código"
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
        editor.addOpenListener {event ->
          event.bean.produto.let {produto ->
            val locSulfixos =
              produto.localizacoes(abreviacaoDefault)
                .map {LocProduto(it)}
            comboLoc.setItems(locSulfixos)
            comboLoc.setItemCaptionGenerator {it.localizacao}
            comboLoc.value = event.bean.localizacao
          }
        }
        val nav = FastNavigation<ProdutoNotaVo>(this, false, true)
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

