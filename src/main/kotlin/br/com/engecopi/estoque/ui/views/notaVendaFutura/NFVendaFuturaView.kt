package br.com.engecopi.estoque.ui.views.notaVendaFutura

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.entregaFutura.INFVendaFuturaView
import br.com.engecopi.estoque.viewmodel.entregaFutura.NFVendaFuturaViewModel
import br.com.engecopi.estoque.viewmodel.entregaFutura.NFVendaFuturaVo
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.framework.ui.view.timeFormat
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Button
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("nf_venda_futura")
class NFVendaFuturaView: CrudLayoutView<NFVendaFuturaVo, NFVendaFuturaViewModel>(), INFVendaFuturaView {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = RegistryUserInfo.userDefaultIsAdmin
  
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }
  
  init {
    viewModel = NFVendaFuturaViewModel(this)
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
    form("Nota Fiscal (Entrega Futura)")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      updateOperationVisible = false
      addOperationVisible = false
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(NFVendaFuturaVo::numero) {
        caption = "Número NF"
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
      column(NFVendaFuturaVo::loja) {
        caption = "Loja NF"
        setRenderer({loja ->
                      loja?.sigla ?: ""
                    }, TextRenderer())
      }
      column(NFVendaFuturaVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({tipo ->
                      tipo?.descricao ?: ""
                    }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(NFVendaFuturaVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(NFVendaFuturaVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      
      column(NFVendaFuturaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(NFVendaFuturaVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(NFVendaFuturaVo::usuario) {
        caption = "Usuário"
        setRenderer({
                      it?.loginName ?: ""
                    }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(NFVendaFuturaVo::rota) {
        caption = "Rota"
      }
      column(NFVendaFuturaVo::cliente) {
        caption = "Cliente"
        setSortProperty("cliente")
      }
    }
  }
  
  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Chave da Nota Fiscal") {key ->
      val notaSaida = viewModel.findNotaSaidaKey(key)
      if(notaSaida.isNotEmpty()) {
        val dialog = DlgNotaFuturaLoc(notaSaida, viewModel) {itens ->
          val nota = viewModel.processaKey(itens)
          val pacotes = viewModel.imprimir(nota)
          pacotes.forEach {
            printText(it.impressora, it.text)
          }
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
        val impressora = RegistryUserInfo.usuarioDefault.impressora
        printText(impressora, text)
        //grid.refreshGrid()
      }
    }
  }
}

