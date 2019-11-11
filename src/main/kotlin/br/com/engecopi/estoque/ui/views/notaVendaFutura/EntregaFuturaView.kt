package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.entregaFutura.EntregaFututaViewModel
import br.com.engecopi.estoque.viewmodel.entregaFutura.EntregaFututaVo
import br.com.engecopi.estoque.viewmodel.entregaFutura.IEntregaFututaView
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("entrega_futura")
class EntregaFuturaView: NotaView<EntregaFututaVo, EntregaFututaViewModel, IEntregaFututaView>(),
                         IEntregaFututaView {
  var formCodBar: PnlCodigoBarras? = null

  override fun enter(event: ViewChangeEvent) {
    super.enter(event)
    formCodBar?.focusEdit()
  }

  init {
    viewModel = EntregaFututaViewModel(this)
    layoutForm {
      if(operation == ADD) {
        binder.bean.lojaNF = lojaDefault
        binder.bean.usuario = usuario
      }
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
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
                bind(binder).bind(EntregaFututaVo::tipoNota)
              }
              dateField("Data") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaFututaVo::dataNota.name)
              }
              textField("Rota") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaFututaVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expandRatio = 1f
                bind(binder).bind(EntregaFututaVo::observacaoNota)
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
    form("Entrega ao Cliente")
    gridCrud {
      formCodBar = formCodbar()
      addCustomToolBarComponent(btnDesfazer())
      addCustomFormComponent(formCodBar)
      reloadOnly = !isAdmin
      column(EntregaFututaVo::numeroCodigo) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(EntregaFututaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja -> loja?.sigla ?: ""}, TextRenderer())
      }
      column(EntregaFututaVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntregaFututaVo::lancamento) {
        caption = "Lançamento"
        dateFormat()
        setSortProperty("nota.lancamento", "data", "hora")
      }
      column(EntregaFututaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntregaFututaVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntregaFututaVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntregaFututaVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntregaFututaVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntregaFututaVo::localizacao) {
        caption = "Localização"
        setRenderer({it?.toString()}, TextRenderer())
      }
      column(EntregaFututaVo::usuario) {
        caption = "Usuário"
        setRenderer({it?.loginName ?: ""}, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntregaFututaVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntregaFututaVo::cliente) {
        caption = "Cliente"
        setSortProperty("nota.cliente")
      }
      val itens = viewModel.notasConferidas()
        .groupBy {it.numeroNF}
        .entries.sortedBy {entry ->
        entry.value.map {it.entityVo?.id ?: 0}
          .max()
      }
        .mapNotNull {it.key}

      grid.setStyleGenerator {saida ->
        if(saida.status == CONFERIDA) {
          val numero = saida.numeroNF
          val index = itens.indexOf(numero)
          if(index % 2 == 0) "pendente"
          else "pendente2"
        }
        else null
      }
    }
  }

  private fun formCodbar(): PnlCodigoBarras {
    return PnlCodigoBarras("Código de barras") {key ->
      viewModel.processaKey(key)
    }
  }
}