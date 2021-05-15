package br.com.engecopi.estoque.ui.views.entregaFutura

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.views.movimentacao.NotaView
import br.com.engecopi.estoque.viewmodel.entregaFutura.EditorFuturaViewModel
import br.com.engecopi.estoque.viewmodel.entregaFutura.EntregaFututaVo
import br.com.engecopi.estoque.viewmodel.entregaFutura.IEditorFuturaView
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.shared.ui.ContentMode.HTML
import com.vaadin.ui.Alignment
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("editor_futura") class EditorFuturaView : NotaView<EntregaFututaVo, EditorFuturaViewModel, IEditorFuturaView>(
  true), IEditorFuturaView {
  init {
    viewModel = EditorFuturaViewModel(this)
    layoutForm {
      if (operation == ADD) {
        binder.bean.lojaNF = lojaDeposito
        binder.bean.usuario = usuario
      }
      formLayout.apply {
        w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt().px

        grupo("Nota fiscal de saída") {
          verticalLayout {
            row {
              notaFiscalField(operation, binder)
              lojaField(operation, binder)
              comboBox<TipoNota>("Tipo") {
                expandRatio = 2f
                default { it.descricao }
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
        row {
          label("<b>Produto</b>") {
            contentMode = HTML
          }

          addComponent(footerLayout)
          setComponentAlignment(footerLayout, Alignment.BOTTOM_LEFT)
          addComponentsAndExpand(Label(""))
        }
        grupo {
          produtoField(operation, binder, "Saída")
        }
      }
      if (!isAdmin && operation == UPDATE) binder.setReadOnly(true)
    }
    form("Editor Entrega Futura")
    gridCrud {
      reloadOnly = !isAdmin
      addCustomToolBarComponent(btnDesfazer())
      column(EntregaFututaVo::numeroCodigo) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(EntregaFututaVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntregaFututaVo::numeroBaixa) {
        caption = "NF Baixa"
      }
      column(EntregaFututaVo::dataBaixa) {
        caption = "Data Baixa"
        dateFormat()
      }
      column(EntregaFututaVo::lancamento) {
        caption = "Lançamento"
        dateFormat()
        setSortProperty("nota.lancamento")
      }
      column(EntregaFututaVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("nota.lancamento", "nota.hora")
      }
      column(EntregaFututaVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({ loja -> loja?.sigla ?: "" }, TextRenderer())
      }
      column(EntregaFututaVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntregaFututaVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntregaFututaVo::status) {
        caption = "Situação"
        setRenderer({ it?.descricao ?: "" }, TextRenderer())
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
        setRenderer({ it?.abreviacao }, TextRenderer())
      }
      column(EntregaFututaVo::usuario) {
        caption = "Usuário"
        setRenderer({ it?.loginName ?: "" }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntregaFututaVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntregaFututaVo::cliente) {
        caption = "Cliente"
        setSortProperty("nota.cliente")
      }
      val itens = viewModel.notasConferidas().groupBy { it.nota?.numero }.entries.sortedBy { entry ->
        entry.value.map { it.id }.maxOrNull()
      }.mapNotNull { it.key }

      grid.setStyleGenerator { saida ->
        if (saida.status == CONFERIDA) {
          val numero = saida.numeroNF
          val index = itens.indexOf(numero)
          if (index % 2 == 0) "pendente"
          else "pendente2"
        }
        else null
      }
    }
  }
}