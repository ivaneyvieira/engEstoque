package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.ProdutoVO
import br.com.engecopi.estoque.viewmodel.ProdutoViewModel
import br.com.engecopi.estoque.viewmodel.ProdutoVo
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.bindItens
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.reload
import br.com.engecopi.framework.ui.view.reloadBinderOnChange
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.VAlign
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.checkBoxGroup
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.h
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.ui.ComboBox
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.LocalDateRenderer
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import org.vaadin.patrik.FastNavigation
import java.text.DecimalFormat

@AutoView
class ProdutoView: CrudLayoutView<ProdutoVo, ProdutoViewModel>() {
  init {
    viewModel = ProdutoViewModel(this)
    isAddClose = false
    layoutForm {
      binder.bean.lojaDefault = RegistryUserInfo.lojaDefault
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px
        h = 300.px
        grupo("Produtos") {
          row {
            textField {
              expand = 1
              caption = "Código"
              bind(binder).bind(ProdutoVo::codigoProduto)
              reloadBinderOnChange(binder)
            }
            textField("Descrição") {
              expand = 3
              isReadOnly = true
              bind(binder).bind(ProdutoVo::descricaoProdutoSaci.name)
            }
            if(operation != ADD) {
              textField {
                expand = 1
                caption = "Grade"
                bind(binder).bind(ProdutoVo::grade.name)
                reloadBinderOnChange(binder)
              }
            }
          }
          if(operation == ADD) {
            row {
              checkBoxGroup<String> {
                expand = 1
                caption = "Grades"
                addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL)
                bindItens(binder, ProdutoVo::grades.name)
                bind(binder).bind(ProdutoVo::gradesProduto)
              }
            }
          }
        }
        grupo("Notas") {
          row {
            dateField("Data Inicial") {
              expand = 1
              id = "filtro"
              value = null
              bind(binder).bind(ProdutoVo::filtroDI)
              reloadBinderOnChange(binder)
            }
            dateField("Data Final") {
              expand = 1
              id = "filtro"
              value = null
              bind(binder).bind(ProdutoVo::filtroDF)
              reloadBinderOnChange(binder)
            }
            comboBox<TipoNota>("Tipo") {
              expand = 1
              default {it.descricao2}
              id = "filtro"
              setItems(TipoNota.values().toList())
              isEmptySelectionAllowed = true
              emptySelectionCaption = "Todos"
              value = null
              bind(binder).bind(ProdutoVo::filtroTipo)
              reloadBinderOnChange(binder)
            }
            comboBox<LocProduto>("Local") {
              expand = 2
              default {it.localizacao}
              isEmptySelectionAllowed = true
              id = "filtro"
              val itens = viewModel.localizacoes(binder.bean)
              emptySelectionCaption = "Todos"
              setItems(itens)
              bind(binder).bind(ProdutoVo::filtroLocalizacao)
              value = itens.firstOrNull()
              reloadBinderOnChange(binder)
            }
          }
          row {
            grid(ItemNota::class) {
              expand = 2
              removeAllColumns()
              editor.isEnabled = usuarioDefault.admin
              val comboLoc = ComboBox<String>().apply {
                isEmptySelectionAllowed = false
                isTextInputAllowed = false
              }
              addColumnFor(ItemNota::numeroNota) {
                this.isSortable = false
                caption = "Nota"
              }
              addColumnFor(ItemNota::dataNota) {
                this.isSortable = false
                caption = "Data Nota"
                setRenderer(LocalDateRenderer("dd/MM/yy"))
              }
              addColumnFor(ItemNota::data) {
                this.isSortable = false
                caption = "Data Inserção"
                setRenderer(LocalDateRenderer("dd/MM/yy"))
              }
              addColumnFor(ItemNota::tipoNota) {
                this.isSortable = false
                caption = "Tipo"
                setRenderer({it?.descricao ?: ""}, TextRenderer())
              }
              addColumnFor(ItemNota::rota) {
                this.isSortable = false
                caption = "Rota"
              }
              addColumnFor(ItemNota::localizacao) {
                this.isSortable = false
                caption = "Local"
                setEditorComponent(comboLoc)
              }
              addColumnFor(ItemNota::quantidadeSaldo) {
                this.isSortable = false
                caption = "Quantidade"
                setRenderer(NumberRenderer(DecimalFormat("0")))
                align = VAlign.Right
              }
              addColumnFor(ItemNota::saldo) {
                this.isSortable = false
                caption = "Saldo"
                setRenderer(NumberRenderer(DecimalFormat("0")))
                align = VAlign.Right
              }
              editor.addOpenListener { event ->
                event.bean.produto?.let { produto ->
                  val locSulfixos = produto.localizacoes().map { LocProduto(it) }
                  comboLoc.setItems(locSulfixos.map {it.localizacao})
                  comboLoc.value = event.bean.localizacao
                }
              }
              editor.addSaveListener {
                val item = it.bean
                viewModel.saveItem(item)
                binder.reload()
              }
            //  val nav = FastNavigation(this, false, true)
            //  nav.changeColumnAfterLastRow = true
             // nav.openEditorWithSingleClick = true
             // nav.allowArrowToChangeRow = true
             // nav.openEditorOnTyping = true
             // nav.addEditorSaveShortcut(KeyCode.ENTER)
              editor.cancelCaption = "Cancelar"
              editor.saveCaption = "Salvar"
              editor.isBuffered = true
              bindItens(binder, "itensNota")
            }
          }
        }
      }
      if(!RegistryUserInfo.usuarioDefault.admin && operation == UPDATE) binder.setReadOnly(true)
      readButton.isVisible = true
    }
    form("Entrada de produtos")
    gridCrud {
      queryOnly = !RegistryUserInfo.usuarioDefault.admin
      //grid.bodyRowHeight = 3 * 30.00
      column(ProdutoVo::codigoProduto) {
        expandRatio = 1
        caption = "Código"
        setSortProperty("codigo")
      }
      column(ProdutoVo::descricaoProduto) {
        expandRatio = 5
        caption = "Descrição"
        setSortProperty("vproduto.nome")
      }
      column(ProdutoVo::grade) {
        expandRatio = 1
        caption = "Grade"
        setSortProperty("grade")
      }
      column(ProdutoVo::localizacao) {
        expandRatio = 1
        //setRenderer({ e -> e?.replace(" - ", " / ") }, HtmlRenderer())
        caption = "Localização"
        setSortProperty("localizacao")
      }
      column(ProdutoVo::saldo) {
        expandRatio = 1
        caption = "Saldo"
        setSortProperty("saldo_total")
        setRenderer(NumberRenderer(DecimalFormat("0")))
        align = VAlign.Right
      }
      column(ProdutoVo::comprimento) {
        expandRatio = 1
        caption = "Comprimento"
        setSortProperty("vproduto.comp")
        setRenderer(NumberRenderer(DecimalFormat("0")))
        align = VAlign.Right
      }
      column(ProdutoVo::lagura) {
        expandRatio = 1
        caption = "Largura"
        setSortProperty("vproduto.larg")
        setRenderer(NumberRenderer(DecimalFormat("0")))
        align = VAlign.Right
      }
      column(ProdutoVo::altura) {
        expandRatio = 1
        caption = "Altura"
        setSortProperty("vproduto.alt")
        setRenderer(NumberRenderer(DecimalFormat("0")))
        align = VAlign.Right
      }
      column(ProdutoVo::cubagem) {
        expandRatio = 1
        caption = "Cubagem"
        setSortProperty("vproduto.cubagem")
        setRenderer(NumberRenderer(DecimalFormat("0.000000")))
        align = VAlign.Right
      }
    }
  }
}


