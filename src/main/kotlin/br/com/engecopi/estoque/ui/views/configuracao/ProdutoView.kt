package br.com.engecopi.estoque.ui.views.configuracao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.configuracao.IProdutoView
import br.com.engecopi.estoque.viewmodel.configuracao.ProdutoViewModel
import br.com.engecopi.estoque.viewmodel.configuracao.ProdutoVo
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.data.Binder
import com.vaadin.data.HasValue
import com.vaadin.ui.Alignment
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.NumberRenderer
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import java.text.DecimalFormat

@AutoView
class ProdutoView : CrudLayoutView<ProdutoVo, ProdutoViewModel>(true), IProdutoView {
  init {
    viewModel = ProdutoViewModel(this)
    isAddClose = false
    layoutForm {
      binder.bean.lojaDefault = lojaDeposito
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.9).toInt().px
        h = 300.px
        grupo("Produtos") {
          row {
            textField {
              expandRatio = 1f
              caption = "Código"
              bind(binder).bind(ProdutoVo::codigoProduto)
              reloadBinderOnChangeAndScroll(binder)
            }
            textField("Descrição") {
              expandRatio = 4f
              isReadOnly = true
              bind(binder).bind(ProdutoVo::descricaoProdutoSaci.name)
            }
            textField("Garantia") {
              expandRatio = 1f
              isReadOnly = true
              bind(binder).bind(ProdutoVo::meseGarantiaStr.name)
            }
            integerField("Quant. Pac") {
              expandRatio = 1f
              isReadOnly = true
              bind(binder).bind(ProdutoVo::quantidadePacote.name)
            }
            if (operation != ADD) {
              textField {
                expandRatio = 1f
                caption = "Grade"
                bind(binder).bind(ProdutoVo::grade.name)
                reloadBinderOnChangeAndScroll(binder)
              }
            }
          }

          row {
            if (operation == ADD) {
              button("Todas as grade") { //expandRatio = 1f
                //addStyleName(ValoTheme.)
                onLeftClick { _ ->
                  val produto = binder.bean
                  val grades = produto.findGradesSaci().toSet()
                  produto.gradesProduto = grades
                  binder.bean = produto
                }
              }
            }
            addComponent(footerLayout)
            setComponentAlignment(footerLayout, Alignment.BOTTOM_LEFT)
            addComponentsAndExpand(Label(""))
          }
          if (operation == ADD) {
            row {
              checkBoxGroup<String> {
                expandRatio = 1f
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
              expandRatio = 1f
              id = "filtro"
              value = null
              bind(binder).bind(ProdutoVo::filtroDI)
              reloadBinderOnChangeAndScroll(binder)
            }
            dateField("Data Final") {
              expandRatio = 1f
              id = "filtro"
              value = null
              bind(binder).bind(ProdutoVo::filtroDF)
              reloadBinderOnChangeAndScroll(binder)
            }
            comboBox<TipoNota>("Tipo") {
              expandRatio = 1f
              default { it.descricao2 }
              id = "filtro"
              setItems(TipoNota.values().toList())
              isEmptySelectionAllowed = true
              emptySelectionCaption = "Todos"
              value = null
              bind(binder).bind(ProdutoVo::filtroTipo)
              reloadBinderOnChangeAndScroll(binder)
            }
            comboBox<LocProduto>("Local") {
              expandRatio = 2f
              default { it.localizacao }
              isEmptySelectionAllowed = true
              id = "filtro"
              val itens = viewModel.localizacoes(binder.bean)
              emptySelectionCaption = "Todos"
              setItems(itens)
              bind(binder).bind(ProdutoVo::filtroLocalizacao)
              value = itens.firstOrNull()
              reloadBinderOnChangeAndScroll(binder)
            }
          }
          row {
            grid(ItemNota::class) {
              setSizeFull()
              h = 20.em
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
                dateFormat()
              }
              addColumnFor(ItemNota::numeroEntrega) {
                this.isSortable = false
                caption = "NF Baixa"
                this.setRenderer({ lista ->
                                   lista.joinToString(" ") { nota ->
                                     nota.numero
                                   }
                                 }, TextRenderer())
              }
              addColumnFor(ItemNota::dataEntrega) {
                caption = "Data Baixa"
                dateFormat()
              }
              addColumnFor(ItemNota::data) {
                this.isSortable = false
                caption = "Data lançamento"
                dateFormatNotNull()
              }
              addColumnFor(ItemNota::dataFabricacao) {
                this.isSortable = false
                caption = "Fabricacao"
                mesAnoFormat()
              }
              addColumnFor(ItemNota::dataValidade) {
                this.isSortable = false
                caption = "Validade"
                mesAnoFormat()
              }
              addColumnFor(ItemNota::tipoNota) {
                this.isSortable = false
                caption = "Tipo"
                setRenderer({ it?.descricao ?: "" }, TextRenderer())
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
                  val locSulfixos = produto.localizacoes(abreviacaoDefault).map { LocProduto(it) }
                  comboLoc.setItems(locSulfixos.map { it.localizacao })
                  comboLoc.value = event.bean.localizacao
                }
              }
              editor.addSaveListener {
                val item = it.bean
                viewModel.saveItem(item)
                binder.reload()
              }

              editor.cancelCaption = "Cancelar"
              editor.saveCaption = "Salvar"
              editor.isBuffered = true
              bindItens(binder, ProdutoVo::itensNota.name)
              binder.addValueChangeListener {
                this.scrollToEnd()
              }
              this.scrollToEnd()
            }
          }
        }
      }
      if (!usuarioDefault.admin && operation == UPDATE) binder.setReadOnly(true)
      readButton.isVisible = true
    }
    form("Entrada de produtos")
    gridCrud {
      queryOnly = !RegistryUserInfo.usuarioDefault.admin //grid.bodyRowHeight = 3 * 30.00
      column(ProdutoVo::codigoProduto) {
        expandRatio = 1
        caption = "Código"
        setSortProperty("codigo")
      }
      column(ProdutoVo::codebar) {
        expandRatio = 1
        caption = "Código Barras"
        setSortProperty("codebar")
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
        caption = "Localização"
        setSortProperty("localizacao")
      }
      column(ProdutoVo::meseGarantiaStr) {
        expandRatio = 1
        caption = "Garantia"
        setSortProperty("meses_vencimento")
      }
      column(ProdutoVo::quantidadePacote) {
        expandRatio = 1
        caption = "Quant. Pac."
        align = VAlign.Right
        setSortProperty("meses_vencimento")
      }
      column(ProdutoVo::saldo) {
        expandRatio = 1
        caption = "Saldo"
        setSortProperty("saldo_total")
        setRenderer(NumberRenderer(DecimalFormat("0")))
        align = VAlign.Right
      }
    /*  column(ProdutoVo::comprimento) {
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
       */
    }
  }

  inline fun <reified BEAN : Any, FIELDVALUE> HasValue<FIELDVALUE>.reloadBinderOnChangeAndScroll(binder: Binder<BEAN>) {
    reloadBinderOnChange(binder)
  }
}



