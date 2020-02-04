package br.com.engecopi.estoque.ui.views.ressuprimento

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.views.PnlCodigoBarras
import br.com.engecopi.estoque.ui.views.movimentacao.NotaView
import br.com.engecopi.estoque.viewmodel.ressuprimento.EntregaRessuprimentoVo
import br.com.engecopi.estoque.viewmodel.ressuprimento.IPedidoRessuprimentoView
import br.com.engecopi.estoque.viewmodel.ressuprimento.PedidoRessuprimentoViewModel
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.timeFormat
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.refresh
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("pedido_ressuprimento")
class PedidoRessuprimentoView:
  NotaView<EntregaRessuprimentoVo, PedidoRessuprimentoViewModel, IPedidoRessuprimentoView>(),
  IPedidoRessuprimentoView {
  var formCodBar: PnlCodigoBarras? = null
  
  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }
  
  init {
    viewModel = PedidoRessuprimentoViewModel(this)
    layoutForm {
      if(operation == ADD) {
        binder.bean.lojaNF = RegistryUserInfo.lojaDeposito
        binder.bean.usuario = usuario
      }
      formLayout.apply {
        w =
          (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
            .px
        
        grupo("Nota fiscal de saída") {
          verticalLayout {
            row {
              notaFiscalField(operation, binder)
              lojaField(operation, binder)
              comboBox<TipoNota>("Tipo") {
                expandRatio = 2f
                default {it.descricao}
                isReadOnly = true
                setItems(TipoNota.valuesSaida())
                bind(binder).bind(EntregaRessuprimentoVo::tipoNota)
              }
              dateField("Data") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaRessuprimentoVo::dataNota.name)
              }
              textField("Rota") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaRessuprimentoVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expandRatio = 1f
                bind(binder).bind(EntregaRessuprimentoVo::observacaoNota)
              }
            }
          }
        }
        
        grupo("Produto") {
          produtoField(operation, binder, "Saída")
        }
      }
      if(!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Pedido de Ressuprimento")
    gridCrud {
      formCodBar = formCodbar()
      addCustomToolBarComponent(btnDesfazer())
      addCustomFormComponent(formCodBar)
      reloadOnly = !isAdmin
      column(EntregaRessuprimentoVo::numeroCodigo) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(EntregaRessuprimentoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntregaRessuprimentoVo::numeroBaixa) {
        caption = "NF Baixa"
      }
      column(EntregaRessuprimentoVo::dataBaixa) {
        caption = "Data Baixa"
        dateFormat()
      }
      column(EntregaRessuprimentoVo::lancamento) {
        caption = "Lançamento"
        dateFormat()
        setSortProperty("nota.lancamento", "data", "hora")
      }
      column(EntregaRessuprimentoVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("nota.lancamento", "nota.hora")
      }
      column(EntregaRessuprimentoVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja -> loja?.sigla ?: ""}, TextRenderer())
      }
      column(EntregaRessuprimentoVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntregaRessuprimentoVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntregaRessuprimentoVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntregaRessuprimentoVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntregaRessuprimentoVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntregaRessuprimentoVo::localizacao) {
        caption = "Localização"
        setRenderer({it?.toString()}, TextRenderer())
      }
      column(EntregaRessuprimentoVo::usuario) {
        caption = "Usuário"
        setRenderer({it?.loginName ?: ""}, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntregaRessuprimentoVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntregaRessuprimentoVo::cliente) {
        caption = "Cliente"
        setSortProperty("nota.cliente")
      }
    }
  }
  
  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Pedido de ressuprimento") {key ->
      viewModel.findKey(key)
    }
  }
  
  override fun updateGrid() {
    grid.refresh()
  }
}

