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
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isMargin
import com.github.mvysny.karibudsl.v8.textField
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.renderers.TextRenderer
import org.vaadin.viritin.fields.IntegerField

@AutoView
class LabelView: LayoutView<LabelViewModel>() {
  private lateinit var gridProduto: Grid<Produto>
  private lateinit var cmbTipoFiltro: ComboBox<FiltroView>
  private lateinit var pnlFiltro: HorizontalLayout

  init {
    viewModel = LabelViewModel(this)
    val filtroFaixaCodigo = FiltroFaixaCodigo(viewModel)
    val filtroFaixaNome = FiltroFaixaNome(viewModel)
    val filtroFabricante = FiltroFabricante(viewModel)
    val filtroCentroLucro = FiltroCentroLucro(viewModel)
    val filtroTipoProduto = FiltroTipoProduto(viewModel)
    val filtroCodigoGrade = FiltroCodigoGrade(viewModel)
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
    grupo("Produtos") {
      gridProduto = grid(Produto::class) {
        addColumnFor(Produto::codigo) {
          caption = "Código"
          setRenderer({it?.trim() ?: ""}, TextRenderer())
        }
        addColumnFor(Produto::descricao) {
          caption = "Descrição"
          this.expandRatio = 1
        }
        addColumnFor(Produto::grade) {
          caption = "Grade"
        }
        addColumnFor(Produto::barcodeGtin) {
          caption = "Gtin"
        }
      }
    }
  }

  override fun updateView() {
    viewModel.run {}
  }

  override fun updateModel() {
    viewModel.run {
      gridProduto.setItems(listaProduto)
    }
  }
}

abstract class FiltroView(val viewModel: LabelViewModel, val descricao: String): VerticalLayout() {
  init {
    isSpacing = false
    isMargin = false
  }
}

class FiltroFaixaCodigo(viewModel: LabelViewModel): FiltroView(viewModel, "Faixa de Código") {
  private lateinit var edtCodigoI: IntegerField
  private lateinit var edtCodigoF: IntegerField

  init {
    row {
      edtCodigoI = integerField("Código Inicial")
      edtCodigoF = integerField("Código Final")
      button("Adicionar") {
        alignment = Alignment.BOTTOM_RIGHT
        addClickListener {
          viewModel.addFaixaCodigo(edtCodigoI.value, edtCodigoF.value)
        }
      }
    }
  }
}

class FiltroFaixaNome(viewModel: LabelViewModel): FiltroView(viewModel, "Faixa de Nome") {
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

class FiltroFabricante(viewModel: LabelViewModel): FiltroView(viewModel, "Fabricante") {
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

class FiltroCentroLucro(viewModel: LabelViewModel): FiltroView(viewModel, "Centro de lucro") {
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

class FiltroTipoProduto(viewModel: LabelViewModel): FiltroView(viewModel, "Tipo de produto") {
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

class FiltroCodigoGrade(viewModel: LabelViewModel): FiltroView(viewModel, "Código e grade") {
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


