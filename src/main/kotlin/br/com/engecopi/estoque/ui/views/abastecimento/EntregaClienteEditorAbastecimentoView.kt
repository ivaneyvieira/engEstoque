package br.com.engecopi.estoque.ui.views.abastecimento

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.ui.views.movimentacao.NotaView
import br.com.engecopi.estoque.viewmodel.abastecimento.EntregaClienteAbastecimentoVo
import br.com.engecopi.estoque.viewmodel.abastecimento.EntregaClienteEditorAbastecimentoViewModel
import br.com.engecopi.estoque.viewmodel.abastecimento.IEntregaClienteEditorAbastecimentoView
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
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.ui.UI
import com.vaadin.ui.renderers.TextRenderer

@AutoView("entrega_cliente_editor")
class EntregaClienteEditorAbastecimentoView:
  NotaView<EntregaClienteAbastecimentoVo, EntregaClienteEditorAbastecimentoViewModel, IEntregaClienteEditorAbastecimentoView>(),
  IEntregaClienteEditorAbastecimentoView {
  init {
    viewModel = EntregaClienteEditorAbastecimentoViewModel(this)
    layoutForm {
      if(operation == ADD) {
        binder.bean.lojaNF = lojaDeposito
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
                bind(binder).bind(EntregaClienteAbastecimentoVo::tipoNota)
              }
              dateField("Data") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaClienteAbastecimentoVo::dataNota.name)
              }
              textField("Rota") {
                expandRatio = 1f
                isReadOnly = true
                bind(binder).bind(EntregaClienteAbastecimentoVo::rota)
              }
            }
            row {
              textField("Observação da nota fiscal") {
                expandRatio = 1f
                bind(binder).bind(EntregaClienteAbastecimentoVo::observacaoNota)
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
      column(EntregaClienteAbastecimentoVo::numeroCodigoReduzido) {
        caption = "Número Conferencia"
        setSortProperty("codigo_barra_conferencia")
      }
      column(EntregaClienteAbastecimentoVo::lojaNF) {
        caption = "Loja NF"
        setRenderer({loja -> loja?.sigla ?: ""}, TextRenderer())
      }
      column(EntregaClienteAbastecimentoVo::tipoNotaDescricao) {
        caption = "TipoNota"
        setSortProperty("nota.tipo_nota")
      }
      column(EntregaClienteAbastecimentoVo::lancamento) {
        caption = "Lançamento"
        dateFormat()
        setSortProperty("nota.lancamento")
      }
      column(EntregaClienteAbastecimentoVo::horaLacamento) {
        caption = "Hora"
        timeFormat()
        setSortProperty("nota.lancamento", "nota.hora")
      }
      column(EntregaClienteAbastecimentoVo::dataEmissao) {
        caption = "Emissao"
        dateFormat()
        setSortProperty("nota.dataEmissao", "data", "hora")
      }
      column(EntregaClienteAbastecimentoVo::quantProduto) {
        caption = "Quantidade"
        intFormat()
      }
      column(EntregaClienteAbastecimentoVo::status) {
        caption = "Situação"
        setRenderer({it?.descricao ?: ""}, TextRenderer())
      }
      column(EntregaClienteAbastecimentoVo::codigo) {
        caption = "Código"
        setSortProperty("produto.codigo")
      }
      column(EntregaClienteAbastecimentoVo::descricaoProduto) {
        caption = "Descrição"
      }
      column(EntregaClienteAbastecimentoVo::grade) {
        caption = "Grade"
        setSortProperty("produto.grade")
      }
      column(EntregaClienteAbastecimentoVo::localizacao) {
        caption = "Localização"
        setRenderer({it?.abreviacao}, TextRenderer())
      }
      column(EntregaClienteAbastecimentoVo::usuario) {
        caption = "Usuário"
        setRenderer({it?.loginName ?: ""}, TextRenderer())
        setSortProperty("usuario.loginName")
      }
      column(EntregaClienteAbastecimentoVo::rotaDescricao) {
        caption = "Rota"
      }
      column(EntregaClienteAbastecimentoVo::cliente) {
        caption = "Cliente"
        setSortProperty("nota.cliente")
      }
      val itens =
        viewModel.notasConferidas()
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
}