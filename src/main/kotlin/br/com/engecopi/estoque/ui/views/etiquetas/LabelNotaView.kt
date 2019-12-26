package br.com.engecopi.estoque.ui.views.etiquetas

import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.ui.print.PrintUtil
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta.LANCAMENTO
import br.com.engecopi.estoque.viewmodel.etiquetas.ILabelNotaView
import br.com.engecopi.estoque.viewmodel.etiquetas.LabelNotaViewModel
import br.com.engecopi.estoque.viewmodel.etiquetas.NotaLabelVo
import br.com.engecopi.framework.ui.view.LayoutView
import br.com.engecopi.framework.ui.view.dateFormat
import br.com.engecopi.framework.ui.view.default
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.AutoView
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.comboBox
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.getAll
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.isExpanded
import com.github.mvysny.karibudsl.v8.isMargin
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField

@AutoView
class LabelNotaView: LayoutView<LabelNotaViewModel>(), ILabelNotaView {
  private lateinit var edtNumeroNota: TextField
  private lateinit var gridNota: Grid<NotaLabelVo>
  private lateinit var cmbTipoEtiqueta: ComboBox<ETipoEtiqueta>
  
  init {
    viewModel = LabelNotaViewModel(this)
    setSizeFull()
    form("Código de barras nota")
    verticalLayout {
      isExpanded = true
      setSizeFull()
      grupo("Pesquisa Nota") {
        this.row {
          cmbTipoEtiqueta = comboBox("Tipo Filtro") {
            this.expandRatio = 2f
            default()
            val filtrosView =
              ETipoEtiqueta.values()
                .toList()
            setItems(filtrosView)
            setItemCaptionGenerator {it.descricao}
            value = LANCAMENTO
          }
          
          horizontalLayout {
            this.expandRatio = 10f
            isSpacing = false
            isMargin = false
            edtNumeroNota = textField("Numero NF") {
              addValueChangeListener {
                viewModel.processaFiltro()
              }
            }
          }
          
          button("Imprimir") {
            addClickListener {
              val print = viewModel.impressaoNota()
              PrintUtil.printText(RegistryUserInfo.impressoraUsuario, print)
            }
          }
        }
        gridNota = grid(NotaLabelVo::class) {
          isExpanded = true
          setSizeFull()
          this.removeAllColumns()
          addColumnFor(NotaLabelVo::numero) {
            caption = "Número"
          }
          addColumnFor(NotaLabelVo::dataEmissao) {
            caption = "Emissao"
            dateFormat()
          }
          addColumnFor(NotaLabelVo::numeroBaixa) {
            caption = "NF Baixa"
          }
          addColumnFor(NotaLabelVo::dataBaixa) {
            caption = "Data Baixa"
            dateFormat()
          }
          addColumnFor(NotaLabelVo::lancamento) {
            caption = "Data Lançamento"
            dateFormat()
            setSortProperty("data", "hora")
          }
          addColumnFor(NotaLabelVo::localizacao) {
            caption = "Localização"
          }
          addColumnFor(NotaLabelVo::usuario) {
            caption = "Usuário"
          }
          addColumnFor(NotaLabelVo::rotaDescricao) {
            caption = "Rota"
          }
          addColumnFor(NotaLabelVo::cliente) {
            caption = "Cliente"
            setSortProperty("nota.cliente")
          }
        }
      }
    }
  }
  
  override var listaNota: List<NotaLabelVo>
    get() = gridNota.dataProvider.getAll()
    set(value) {
      gridNota.setItems(value)
    }
  override var tipoEtiqueta: ETipoEtiqueta?
    get() = cmbTipoEtiqueta.value
    set(value) {
      cmbTipoEtiqueta.value = value
    }
  override var numeroNota: String?
    get() = edtNumeroNota.value
    set(value) {
      edtNumeroNota.value = value
    }
}

