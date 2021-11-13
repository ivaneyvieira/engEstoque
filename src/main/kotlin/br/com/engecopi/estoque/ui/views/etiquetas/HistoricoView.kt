package br.com.engecopi.estoque.ui.views.etiquetas

import br.com.engecopi.estoque.viewmodel.etiquetas.HistoricoViewModel
import br.com.engecopi.estoque.viewmodel.etiquetas.HistoricoVo
import br.com.engecopi.estoque.viewmodel.etiquetas.IHistoricoView
import br.com.engecopi.framework.ui.view.CrudLayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.timeFormat
import com.github.mvysny.karibudsl.v8.AutoView
import com.vaadin.ui.renderers.TextRenderer

@AutoView
class HistoricoView: CrudLayoutView<HistoricoVo, HistoricoViewModel>(false), IHistoricoView {
  init {
    viewModel = HistoricoViewModel(this)
    layoutForm {
      formLayout.apply {}
    }
    form("Histórico")
    gridCrud {
      deleteOperationVisible = false
      updateOperationVisible = false
      addOperationVisible = false

      column(HistoricoVo::data) {
        expandRatio = 1
        caption = "Data"
        dateFormat()
        setSortProperty("data")
      }
      column(HistoricoVo::datahora) {
        expandRatio = 1
        caption = "Hora"
        timeFormat()
        setSortProperty("data", "hora")
      }
      column(HistoricoVo::usuario) {
        expandRatio = 2
        caption = "Usuário"
        setRenderer({it?.nome}, TextRenderer())
        setSortProperty("usuario.nome")
      }
      column(HistoricoVo::produto) {
        expandRatio = 1
        caption = "Código"
        setRenderer({it?.codigo?.trim()}, TextRenderer())
        setSortProperty("produto.codigo")
      }
      column(HistoricoVo::produto) {
        expandRatio = 3
        caption = "Nome"
        setRenderer({it?.descricao}, TextRenderer())
      }
      column(HistoricoVo::produto) {
        expandRatio = 1
        caption = "grade"
        setRenderer({it?.grade}, TextRenderer())
        setSortProperty("produto.grade")
      }
      column(HistoricoVo::produto) {
        expandRatio = 4
        caption = "GTIN"
        setRenderer({it?.barcodeGtin}, TextRenderer())
      }
    }
  }
}