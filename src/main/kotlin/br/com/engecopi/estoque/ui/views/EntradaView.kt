package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.RegistryUserInfo.impressora
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.EntradaViewModel
import br.com.engecopi.estoque.viewmodel.EntradaVo
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.integerField
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.timeFormat
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView
class EntradaView: NotaView<EntradaVo, EntradaViewModel>() {
  init {
    viewModel = EntradaViewModel(this)
    layoutForm {
      if(operation == ADD) {
        binder.bean.lojaNF = lojaDefault
        binder.bean.usuario = usuario
      }
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px

        grupo("Nota fiscal de entrada") {
          row {
            notaFiscalField(operation, binder)
            lojaField(operation, binder)
            comboBox<TipoNota>("Tipo") {
              expandRatio = 2f
              default {it.descricao}
              isReadOnly = true
              setItems(TipoNota.valuesEntrada())
              bind(binder).bind(EntradaVo::tipoNota)
            }
            textField("Rota") {
              expandRatio = 1f
              isReadOnly = true
              bind(binder).bind(EntradaVo::rota)
            }
          }
          row {
            textField("Observação") {
              expandRatio = 2f
              bind(binder).bind(EntradaVo::observacaoNota)
            }
          }
          row {
            dateField("Data") {
              expandRatio = 1f
              isReadOnly = true
              bind(binder).bind(EntradaVo::dataNota.name)
            }
            integerField("Número Interno") {
              expandRatio = 1f
              isReadOnly = true
              this.bind(binder)
                .bind(EntradaVo::numeroInterno.name)
            }
            textField("Fornecedor") {
              expandRatio = 2f
              isReadOnly = true
              bind(binder).bind(EntradaVo::fornecedor.name)
            }
          }
        }

        grupo("Produto") {
          produtoField(operation, binder, "Entrada")
        }
      }
      if(!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Entrada de produtos")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      addOnly = !isAdmin
      column(EntradaVo::numeroNF) {
        //isSortable = true
        caption = "Número NF"
        setSortProperty("nota.numero")
      }
      grid.addComponentColumn {item ->
        val button = Button()
        button.isEnabled = isAdmin
        button.icon = VaadinIcons.PRINT
        button.addClickListener {
          item.itemNota?.recalculaSaldos()
          val numero = item.numeroNF
          showQuestion(msg = "Imprimir todos os itens da nota $numero?",
                       execYes = {imprimeItem(item, true)},
                       execNo = {imprimeItem(item, false)})
        }

        button
      }
        .id = "btnPrint"
      column(EntradaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja -> loja?.sigla ?: ""}, TextRenderer())
      }
      column(EntradaVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntradaVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(EntradaVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(EntradaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntradaVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntradaVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntradaVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntradaVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntradaVo::localizacao) {
        caption = "Local"

        setRenderer({it?.localizacao}, TextRenderer())
      }
      column(EntradaVo::usuario) {
        caption = "Usuário"
        setRenderer({it?.loginName ?: ""}, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntradaVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntradaVo::fornecedor) {
        caption = "Fornecedor"
        setSortProperty("nota.fornecedor")
      }
    }
  }

  protected fun imprimeItem(item: EntradaVo, notaCompleta: Boolean) {
    val itemNota = item.itemNota ?: item.findEntity()
    val text = viewModel.imprimir(itemNota, notaCompleta)

    printText(impressora, text)
    refreshGrid()
  }

  override fun processAdd(domainObject: EntradaVo) {
    super.processAdd(domainObject)
    imprimeItem(domainObject, true)
  }
}


