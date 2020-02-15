package br.com.engecopi.estoque.ui.views.ressuprimento

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.ui.print.PrintUtil
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.viewmodel.ressuprimento.IPedidoRessuprimentoView
import br.com.engecopi.estoque.viewmodel.ressuprimento.PedidoRessuprimentoViewModel
import br.com.engecopi.estoque.viewmodel.ressuprimento.PedidoRessuprimentoVo
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
import com.github.mvysny.karibudsl.v8.refresh
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.icons.VaadinIcons.PRINT
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.Button
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("pedido_ressuprimento")
class PedidoRessuprimentoView:
  CrudLayoutView<PedidoRessuprimentoVo, PedidoRessuprimentoViewModel>(), IPedidoRessuprimentoView {
  var formCodBar: PnlCodigoBarras? = null
  private val isAdmin
    get() = RegistryUserInfo.userDefaultIsAdmin
  
  init {
    viewModel = PedidoRessuprimentoViewModel(this)
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
    form("Ressuprimento")
    gridCrud {
      addCustomToolBarComponent(btnImprimeTudo())
      formCodBar = formCodbar()
      addCustomFormComponent(formCodBar)
      updateOperationVisible = false
      addOperationVisible = false
      deleteOperationVisible = RegistryUserInfo.usuarioDefault.admin
      column(PedidoRessuprimentoVo::codigoBarraConferencia) {
        caption = "Chave NF"
        setSortProperty("codigoBarraConferencia")
      }
      column(PedidoRessuprimentoVo::numeroBaixa) {
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
      column(PedidoRessuprimentoVo::loja) {
        caption = "Loja NF"
        setRenderer({loja ->
                      loja?.sigla ?: ""
                    }, TextRenderer())
      }
      column(PedidoRessuprimentoVo::tipoNota) {
        caption = "TipoNota"
        setRenderer({tipo ->
                      tipo?.descricao ?: ""
                    }, TextRenderer())
        setSortProperty("tipo_nota")
      }
      column(PedidoRessuprimentoVo::lancamento) {
        caption = "Data"
        dateFormat()
        setSortProperty("data", "hora")
      }
      column(PedidoRessuprimentoVo::dataHoraLancamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      
      column(PedidoRessuprimentoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("dataEmissao", "data", "hora")
      }
      column(PedidoRessuprimentoVo::abreviacao) {
        caption = "Localização"
        setSortProperty("abreviacao")
      }
      column(PedidoRessuprimentoVo::usuario) {
        caption = "Usuário"
        setRenderer({
                      it?.loginName ?: ""
                    }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(PedidoRessuprimentoVo::rota) {
        caption = "Rota"
      }
      column(PedidoRessuprimentoVo::cliente) {
        caption = "Cliente"
        setSortProperty("cliente")
      }
    }
  }
  
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }
  
  private fun impressora(abreviacao: String?): Printer? {
    abreviacao ?: return Printer("ENTRADA")
    return Abreviacao.findByAbreviacao(abreviacao)
      ?.printer
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

