package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.EntradaViewModel
import br.com.engecopi.estoque.viewmodel.EntradaVo
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.integerField
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Button
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
              expand = 2
              default {it.descricao}
              isReadOnly = true
              setItems(TipoNota.valuesEntrada())
              bind(binder).bind(EntradaVo::tipoNota)
            }
            textField("Rota") {
              expand = 1
              isReadOnly = true
              bind(binder).bind(EntradaVo::rota)
            }
          }
          row {
            textField("Observação") {
              expand = 2
              bind(binder).bind(EntradaVo::observacaoNota)
            }
          }
          row {
            dateField("Data") {
              expand = 1
              isReadOnly = true
              bind(binder).bind(EntradaVo::dataNota.name)
            }
            integerField("Número Interno") {
              expand = 1
              isReadOnly = true
              this.bind(binder)
                .bind(EntradaVo::numeroInterno.name)
            }
            textField("Fornecedor") {
              expand = 2
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
        val impresso = item?.entityVo?.impresso ?: true
        button.isEnabled = impresso == false || isAdmin
        button.icon = VaadinIcons.PRINT
        button.addClickListener {
          item.itemNota?.recalculaSaldos()
          openText(viewModel.imprimir(item.itemNota))
          val print = item?.entityVo?.impresso ?: true
          it.button.isEnabled = print == false || isAdmin
          refreshGrid()
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
      column(EntradaVo::dataNota) {
        caption = "Data Nota"
        dateFormat()

        setSortProperty("nota.data", "data", "hora")
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
}


