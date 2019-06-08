package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.viewmodel.LabelViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.textField
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.ComboBox
import com.vaadin.ui.TextField

@AutoView
class LabelView: LayoutView<LabelViewModel>() {
  private lateinit var edtDescricao: TextField
  private lateinit var edtGrade: ComboBox<String>
  private lateinit var edtCodigo: TextField

  init {
    viewModel = LabelViewModel(this)
    form("Código de barras")
    grupo("Produto") {
      row {
        edtCodigo = textField("Código") {
          this.valueChangeMode = ValueChangeMode.BLUR
          addValueChangeListener {
            viewModel.pesquisaCodigo()
          }
        }
        edtGrade = comboBox("Grade") {}
        edtDescricao = textField("Descricao") {
          expand = 1
          isReadOnly = true
          tabIndex = -1
        }
        button("Imprimir") {
          alignment = Alignment.BOTTOM_RIGHT
        }
      }
    }
  }

  override fun updateView() {
    edtCodigo.value = viewModel.codigo
    edtGrade.setItems(viewModel.listGrade)
    edtGrade.value = viewModel.grade
    edtDescricao.value = viewModel.descricao
  }

  override fun updateModel() {
    viewModel.codigo = edtCodigo.value ?: ""
    viewModel.grade = edtGrade.value ?: ""
  }
}