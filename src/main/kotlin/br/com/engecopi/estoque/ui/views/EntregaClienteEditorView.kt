package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.EntregaClienteEditorViewModel
import br.com.engecopi.estoque.viewmodel.EntregaClienteVo
import br.com.engecopi.framework.ui.view.CrudOperation.ADD
import br.com.engecopi.framework.ui.view.CrudOperation.UPDATE
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.intFormat
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.timeFormat
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.bind
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("entrega_cliente_editor")
class EntregaClienteEditorView: NotaView<EntregaClienteVo, EntregaClienteEditorViewModel>() {
  init {
    viewModel = EntregaClienteEditorViewModel(this)
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
                expand = 2
                default {it.descricao}
                isReadOnly = true
                setItems(TipoNota.valuesSaida())
                bind(binder).bind(EntregaClienteVo::tipoNota)
              }
              dateField("Data") {
                expand = 1
                isReadOnly = true
                bind(binder).bind(EntregaClienteVo::dataNota.name)
              }
              textField("Rota") {
                expand = 1
                isReadOnly = true
                bind(binder).bind(EntregaClienteVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expand = 1
                bind(binder).bind(EntregaClienteVo::observacaoNota)
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
    form("Editor Entrega ao Cliente")
    gridCrud {
      reloadOnly = !isAdmin
      addCustomToolBarComponent(btnDesfazer())
      column(EntregaClienteVo::numeroCodigoReduzido) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(EntregaClienteVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja -> loja?.sigla ?: ""}, TextRenderer())
      }
      column(EntregaClienteVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntregaClienteVo::lancamento) {
        caption = "Lançamento"
        dateFormat()
        setSortProperty("nota.lancamento")
      }
      column(EntregaClienteVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("nota.lancamento", "nota.hora")
      }
      column(EntregaClienteVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntregaClienteVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntregaClienteVo::status){
        caption = "Situação"
        setRenderer({it?.descricao ?: ""}, TextRenderer())
      }
      column(EntregaClienteVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntregaClienteVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntregaClienteVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntregaClienteVo::localizacao) {
        caption = "Localização"
        setRenderer({it?.abreviacao}, TextRenderer())
      }
      column(EntregaClienteVo::usuario) {
        caption = "Usuário"
        setRenderer({it?.loginName ?: ""}, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntregaClienteVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntregaClienteVo::cliente) {
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
        } else null
      }
    }
  }
}