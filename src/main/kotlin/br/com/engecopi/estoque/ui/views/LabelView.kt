package br.com.engecopi.estoque.ui.views

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.viewmodel.LabelViewModel
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.integerField
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isExpanded
import com.github.mvysny.karibudsl.v8.isMargin
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.shared.ui.ValueChangeMode.BLUR
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
    val filtroNfe = FiltroNfe(viewModel)
    val filtrosView = if(RegistryUserInfo.usuarioDefault.admin) listOf(filtroFaixaCodigo,
                                                                       filtroFaixaNome,
                                                                       filtroFabricante,
                                                                       filtroCentroLucro,
                                                                       filtroTipoProduto,
                                                                       filtroNfe,
                                                                       filtroCodigoGrade)
    else listOf(filtroCodigoGrade)
    setSizeFull()
    form("Código de barras")
    verticalLayout {
      isExpanded = true
      setSizeFull()
      grupo("Pesquisa Produto") {
        this.row {
          cmbTipoFiltro = comboBox("Tipo Filtro") {
            this.expandRatio = 2f
            default()
            setItems(filtrosView)
            setItemCaptionGenerator {it.descricao}
            addValueChangeListener {
              if(it.isUserOriginated) {
                viewModel.clearProduto()
                pnlFiltro.removeAllComponents()
                pnlFiltro.addComponentsAndExpand(it.value)
              }
            }
          }
          pnlFiltro = horizontalLayout {
            this.expandRatio = 10f
            isSpacing = false
            isMargin = false
          }

          button("Imprimir") {
            this.expandRatio = 1f
            alignment = Alignment.BOTTOM_RIGHT
            addClickListener {
              cmbTipoFiltro.value?.let {filtroView ->
                filtroView.processaFiltro()
                val print = viewModel.impressao()
                openText(print)
              }
            }
          }
        }
      }
      gridProduto = grid(Produto::class) {
        isExpanded = true
        setSizeFull()
        this.removeAllColumns()
        addColumnFor(Produto::codigo) {
          caption = "Código"
          setRenderer({it?.trim() ?: ""}, TextRenderer())
          this.expandRatio = 1
        }
        addColumnFor(Produto::descricao) {
          caption = "Descrição"
          this.expandRatio = 9
        }
        addColumnFor(Produto::grade) {
          caption = "Grade"
          this.expandRatio = 1
        }
        addColumnFor(Produto::barcodeGtin) {
          caption = "Gtin"
          this.expandRatio = 1
        }
      }
    }

    setFiltro(filtroCodigoGrade)
  }

  override fun updateView() {
    viewModel.run {
      gridProduto.setItems(listaProduto)
    }
  }

  override fun updateModel() {
    viewModel.run { }
  }

  private fun setFiltro(pnlFIltro: FiltroView) {
    cmbTipoFiltro.value = pnlFIltro
    pnlFiltro.removeAllComponents()
    pnlFiltro.addComponentsAndExpand(pnlFIltro)
    viewModel.clearProduto()
  }
}

abstract class FiltroView(val viewModel: LabelViewModel, val descricao: String): VerticalLayout() {
  init {
    isSpacing = false
    isMargin = false
  }

  abstract fun processaFiltro()
}

class FiltroFaixaCodigo(viewModel: LabelViewModel): FiltroView(viewModel, "Faixa de Código") {
  private lateinit var edtCodigoI: IntegerField
  private lateinit var edtCodigoF: IntegerField

  init {
    row {
      edtCodigoI = integerField("Código Inicial") {
        isExpanded = false

        addValueChangeListener {
          processaFiltro()
        }
      }

      edtCodigoF = integerField("Código Final"){
        isExpanded = false

        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro() = viewModel.addFaixaCodigo(edtCodigoI.value, edtCodigoF.value)
}

class FiltroFaixaNome(viewModel: LabelViewModel): FiltroView(viewModel, "Faixa de Nome") {
  private lateinit var edtNomeI: TextField
  private lateinit var edtNomeF: TextField

  init {
    row {
      edtNomeI = textField("Nome Incial") {
        expandRatio = 1f

        valueChangeMode = BLUR
        addValueChangeListener {
          processaFiltro()
        }
      }
      edtNomeF = textField("Nome Final") {
        expandRatio = 1f
        valueChangeMode = BLUR
        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro() = viewModel.addFaixaNome(edtNomeI.value, edtNomeF.value)
}

class FiltroNfe(viewModel: LabelViewModel): FiltroView(viewModel, "NF Entrada") {
  private lateinit var edtNfe: TextField

  init {
    row {
      edtNfe = textField("Numero NF Entrada"){
        valueChangeMode = BLUR
        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro() = viewModel.addFaixaNfe(edtNfe.value)
}

class FiltroFabricante(viewModel: LabelViewModel): FiltroView(viewModel, "Fabricante") {
  private lateinit var edtFabricante: IntegerField

  init {
    row {
      edtFabricante = integerField("Código do Fabricante"){
        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro() = viewModel.addFaixaFabricante(edtFabricante.value)
}

class FiltroCentroLucro(viewModel: LabelViewModel): FiltroView(viewModel, "Centro de lucro") {
  private lateinit var edtCentroLucro: IntegerField

  init {
    row {
      edtCentroLucro = integerField("Centro de lucro"){
        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro() = viewModel.addFaixaCentroLucro(edtCentroLucro.value)
}

class FiltroTipoProduto(viewModel: LabelViewModel): FiltroView(viewModel, "Tipo de produto") {
  private lateinit var edtTipo: IntegerField

  init {
    row {
      edtTipo = integerField("Tipo do Produto"){
        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro() = viewModel.addFaixaTipoProduto(edtTipo.value)
}

class FiltroCodigoGrade(viewModel: LabelViewModel): FiltroView(viewModel, "Código e grade") {
  private lateinit var edtGrade: ComboBox<String>
  private lateinit var edtCodigo: TextField

  init {
    row {
      edtCodigo = textField("Código") {
        this.valueChangeMode = BLUR
        addValueChangeListener {
          if(it.isUserOriginated) {
            val grades = viewModel.pesquisaGrades(it.value)
            edtGrade.setItems(grades)
            edtGrade.value = null
            processaFiltro()
            edtCodigo.value = ""
            edtCodigo.focus()
          }
        }
      }
      edtGrade = comboBox("Grade") {
        this.default()
        addValueChangeListener {
          processaFiltro()
        }
      }
    }
  }

  override fun processaFiltro()  {
    viewModel.addFaixaCodigoGrade(edtCodigo.value, edtGrade.value)
  }
}


