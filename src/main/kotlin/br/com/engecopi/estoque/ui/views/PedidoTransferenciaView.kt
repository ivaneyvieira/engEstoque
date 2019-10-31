package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.etlSaci.ETLPedidos
import br.com.engecopi.estoque.ui.EstoqueUI
import br.com.engecopi.estoque.viewmodel.PedidoTransferenciaViewModel
import br.com.engecopi.estoque.viewmodel.PedidoTransferenciaVo
import br.com.engecopi.estoque.viewmodel.SaidaVo
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.intFormat
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.column
import com.github.mvysny.karibudsl.v8.cssLayout
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.refresh
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Grid
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("pedidosTransf")
class PedidoTransferenciaView: LayoutView<PedidoTransferenciaViewModel>() {
  private var gridPedido: Grid<PedidoTransferenciaVo>? = null

  init {
    viewModel = PedidoTransferenciaViewModel(this)
    setSizeFull()
    form("Pedidos de transferencia reservado")
    horizontalLayout {
      cssLayout {
        button {
          icon = VaadinIcons.REFRESH
          addClickListener {
            viewModel.refresh()
          }
        }
      }
    }
    gridPedido = grid(dataProvider = ListDataProvider(mutableListOf())) {
      expand()

      addColumnFor(PedidoTransferenciaVo::numero) {
        caption = "Pedido"
      }
      addColumnFor(PedidoTransferenciaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja -> loja?.sigla ?: ""}, TextRenderer())
      }
      addColumnFor(PedidoTransferenciaVo::lancamento) {
        caption = "Data"
        dateFormat()
      }
      addColumnFor(PedidoTransferenciaVo::abreviacao) {
        caption = "Abreviação"
      }
      addColumnFor(PedidoTransferenciaVo::rotaDescricao) {
        caption = "Rota"
      }
      addColumnFor(PedidoTransferenciaVo::cliente) {
        caption = "Cliente"
      }
      addColumnFor(PedidoTransferenciaVo::nfTransferencia) {
        caption = "Nota Fiscal"
      }
    }
    ETLPedidos.addListener("updatePedidos") {
      ui?.access {
        viewModel.refresh()
        ui.push()
      }
    }
  }

  override fun updateView() {
    val dataProvider = gridPedido?.dataProvider as? ListDataProvider
    dataProvider?.items?.clear()
    dataProvider?.items?.addAll(viewModel.pedidosTransferencia)
    gridPedido?.refresh()
  }

  override fun updateModel() {
    //Vazio
  }
}