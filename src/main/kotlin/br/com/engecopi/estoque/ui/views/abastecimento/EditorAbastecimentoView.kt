package br.com.engecopi.estoque.ui.views.abastecimento

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.views.movimentacao.NotaView
import br.com.engecopi.estoque.viewmodel.abastecimento.EditorAbastecimentoViewModel
import br.com.engecopi.estoque.viewmodel.abastecimento.EntregaAbastecimentoVo
import br.com.engecopi.estoque.viewmodel.abastecimento.IEditorAbastecimentoView
import br.com.engecopi.framework.ui.view.*
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.shared.ui.ContentMode.HTML
import com.vaadin.ui.Alignment
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("editor_abastecimento")
class EditorAbastecimentoView : NotaView<EntregaAbastecimentoVo, EditorAbastecimentoViewModel, IEditorAbastecimentoView>(
  customFooterLayout = true), IEditorAbastecimentoView {
  init {
    viewModel = EditorAbastecimentoViewModel(this)
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
                bind(binder).bind(EntregaAbastecimentoVo::tipoNota)
              }
              dateField("Data") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaAbastecimentoVo::dataNota.name)
              }
              textField("Rota") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaAbastecimentoVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expandRatio = 1f
                bind(binder).bind(EntregaAbastecimentoVo::observacaoNota)
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
    form("Editor Abastecimento")
    gridCrud {
      reloadOnly = !isAdmin
      addCustomToolBarComponent(btnDesfazer())
      column(EntregaAbastecimentoVo::numeroCodigoReduzido) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(EntregaAbastecimentoVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({ loja -> loja?.sigla ?: "" }, TextRenderer())
      }
      column(EntregaAbastecimentoVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntregaAbastecimentoVo::lancamento) {
        caption = "Lançamento"
        dateFormat()
        setSortProperty("nota.lancamento")
      }
      column(EntregaAbastecimentoVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("nota.lancamento", "nota.hora")
      }
      column(EntregaAbastecimentoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntregaAbastecimentoVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntregaAbastecimentoVo::status) {
        caption = "Situação"
        setRenderer({ it?.descricao ?: "" }, TextRenderer())
      }
      column(EntregaAbastecimentoVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntregaAbastecimentoVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntregaAbastecimentoVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntregaAbastecimentoVo::localizacao) {
        caption = "Localização"
        setRenderer({ it?.abreviacao }, TextRenderer())
      }
      column(EntregaAbastecimentoVo::usuario) {
        caption = "Usuário"
        setRenderer({ it?.loginName ?: "" }, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntregaAbastecimentoVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntregaAbastecimentoVo::cliente) {
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