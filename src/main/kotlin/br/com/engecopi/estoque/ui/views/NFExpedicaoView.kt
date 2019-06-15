package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.NFExpedicaoViewModel
import br.com.engecopi.estoque.viewmodel.NFExpedicaoVo
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.utils.localDate
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.px
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
          this.icon = VaadinIcons.PRINT
          this.addClickListener {
            openText(viewModel.imprimir(item?.entityVo?.nota))
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
          val abreviacoes = itens.map {it.localizacao}
          val nota = viewModel.processaKey(key, abreviacoes)
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
                 val execConfirma: (itens: List<LocalizacaoNota>) -> Unit): Window("Nota de Saída") {
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
                val itens = gridProdutos.selectedItems.toList()
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

            this.dataProvider = ListDataProvider(itens.flatMap {item ->
              val abreviacoes = viewModel.abreviacoes(item.prdno, item.grade)
              return@flatMap abreviacoes.map {abreviacao ->
                LocalizacaoNota(abreviacao)
              }
            }.distinct().sortedBy {it.localizacao})
            removeAllColumns()
            setSelectionMode(MULTI)
            setSizeFull()
            addColumnFor(LocalizacaoNota::localizacao) {
              expandRatio = 1
              caption = "Código"
            }
          }
        }
      }
    }
  }
}

data class LocalizacaoNota(var localizacao: String/*, var selecionado: Boolean*/)
