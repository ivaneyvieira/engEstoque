package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.viewmodel.LabelViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.expand
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.integerField
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isMargin
import com.github.mvysny.karibudsl.v8.textField
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.ComboBox
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import org.vaadin.viritin.fields.IntegerField

@AutoView
class LabelView: LayoutView<LabelViewModel>() {
  private lateinit var cmbTipoFiltro: ComboBox<FiltroView>
  private lateinit var pnlFiltro: HorizontalLayout

  init {
    viewModel = LabelViewModel(this)
    val filtroFaixaCodigo = FiltroFaixaCodigo(viewModel, ::addProduto)
    val filtroFaixaNome = FiltroFaixaNome(viewModel, ::addProduto)
    val filtroFabricante = FiltroFabricante(viewModel, ::addProduto)
    val filtroCentroLucro = FiltroCentroLucro(viewModel, ::addProduto)
    val filtroTipoProduto = FiltroTipoProduto(viewModel, ::addProduto)
    val filtroCodigoGrade = FiltroCodigoGrade(viewModel, ::addProduto)
    val filtrosView = listOf(filtroFaixaCodigo,
                             filtroFaixaNome,
                             filtroFabricante,
                             filtroCentroLucro,
                             filtroTipoProduto,
                             filtroCodigoGrade)

    form("Código de barras")
    grupo("Pesquisa Produto") {
      row {
        cmbTipoFiltro = comboBox("Tipo Filtro") {
          default()
          setItems(filtrosView)
          setItemCaptionGenerator {it.descricao}
          addValueChangeListener {
            if(it.isUserOriginated) {
              pnlFiltro.removeAllComponents()
              pnlFiltro.addComponentsAndExpand(it.value)
            }
          }
        }
        pnlFiltro = horizontalLayout {
          expand = 1
          isSpacing = false
          isMargin = false
        }
        button("Imprimir") {
          alignment = Alignment.BOTTOM_RIGHT
          addClickListener {
            val label = viewModel.templateLabel()

            openText(label)
          }
        }
      }
    }
  }

  override fun updateView() {
    viewModel.run {}
  }

  override fun updateModel() {
    viewModel.run {}
  }

  fun addProduto(produto: Produto) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

abstract class FiltroView(val viewModel: LabelViewModel, val addProduto: (Produto) -> Unit, val descricao: String):
  VerticalLayout() {
  init {
    isSpacing = false
    isMargin = false
  }
}

class FiltroFaixaCodigo(viewModel: LabelViewModel, addProduto: (Produto) -> Unit): FiltroView(viewModel,
                                                                                              addProduto,
                                                                                              "Faixa de Código") {
  private lateinit var edtCodigoI: IntegerField
  private lateinit var edtCodigoF: IntegerField

  init {
    row {
      edtCodigoI = integerField("Código Inicial")
      edtCodigoF = integerField("Código Final")
      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {}
      }
    }
  }
}

class FiltroFaixaNome(viewModel: LabelViewModel, addProduto: (Produto) -> Unit): FiltroView(viewModel,
                                                                                            addProduto,
                                                                                            "Faixa de Nome") {
  private lateinit var edtNomeI: TextField
  private lateinit var edtNomeF: TextField

  init {
    row {
      edtNomeI = textField("Nome Incial")
      edtNomeF = textField("Nome Final")
      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {}
      }
    }
  }
}

class FiltroFabricante(viewModel: LabelViewModel, addProduto: (Produto) -> Unit): FiltroView(viewModel,
                                                                                             addProduto,
                                                                                             "Fabricante") {
  private lateinit var edtFabricante: IntegerField

  init {
    row {
      edtFabricante = integerField("Código")

      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {}
      }
    }
  }
}

class FiltroCentroLucro(viewModel: LabelViewModel, addProduto: (Produto) -> Unit): FiltroView(viewModel,
                                                                                              addProduto,
                                                                                              "Centro de lucro") {
  private lateinit var edtCentroLucro: IntegerField

  init {
    row {
      edtCentroLucro = integerField("Código")

      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {}
      }
    }
  }
}

class FiltroTipoProduto(viewModel: LabelViewModel, addProduto: (Produto) -> Unit): FiltroView(viewModel,
                                                                                              addProduto,
                                                                                              "Tipo de produto") {
  private lateinit var edtTipo: ComboBox<Int>

  init {
    row {
      edtTipo = comboBox("Tipo") {
        this.default()
      }
      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {}
      }
    }
  }
}

class FiltroCodigoGrade(viewModel: LabelViewModel, addProduto: (Produto) -> Unit): FiltroView(viewModel,
                                                                                              addProduto,
                                                                                              "Código e grade") {
  private lateinit var edtGrade: ComboBox<String>
  private lateinit var edtCodigo: TextField

  init {
    row {
      edtCodigo = textField("Código") {
        this.valueChangeMode = ValueChangeMode.BLUR
        addValueChangeListener {
          viewModel.pesquisaCodigo()
        }
      }
      edtGrade = comboBox("Grade") {
        this.default()
        addValueChangeListener {
          if(it.isUserOriginated) {
            viewModel.pesquisaCodigo()
          }
        }
      }
      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {}
      }
    }
  }
}


