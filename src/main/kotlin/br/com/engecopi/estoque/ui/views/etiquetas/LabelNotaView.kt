package br.com.engecopi.estoque.ui.views.etiquetas

import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.ui.print.PrintUtil
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta.LANCAMENTO
import br.com.engecopi.estoque.viewmodel.etiquetas.ILabelNotaView
import br.com.engecopi.estoque.viewmodel.etiquetas.LabelNotaViewModel
import br.com.engecopi.estoque.viewmodel.etiquetas.NotaLabelVo
import br.com.engecopi.framework.model.AppPrinter
import br.com.engecopi.framework.model.PrinterInfo
import br.com.engecopi.framework.ui.view.*
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.shared.ui.ValueChangeMode.BLUR
import com.vaadin.ui.Alignment.BOTTOM_RIGHT
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.SelectionMode
import com.vaadin.ui.TextField

@AutoView class LabelNotaView : LayoutView<LabelNotaViewModel>(), ILabelNotaView {
  private lateinit var cmbImpressora: ComboBox<PrinterInfo>
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
          cmbTipoEtiqueta = comboBox("Tipo Nota") {
            this.expandRatio = 2f
            default()
            val filtrosView = ETipoEtiqueta.values().toList()
            setItems(filtrosView)
            setItemCaptionGenerator { it.descricao }
            value = LANCAMENTO
          }

          horizontalLayout {
            this.expandRatio = 14f
            edtNumeroNota = textField("Numero NF") {
              valueChangeMode = BLUR
              addValueChangeListener {
                viewModel.processaFiltro()
              }
            }
            cmbImpressora = comboBox<PrinterInfo>("Impressora") {
              expandRatio = 2f
              val itens = AppPrinter.printersInfo
              setItems(itens)
              setItemCaptionGenerator { it.description }

              isTextInputAllowed = false
            }
          }

          button("Imprimir") {
            alignment = BOTTOM_RIGHT
            addClickListener {
              viewModel.impressaoNota().forEach { pacote ->
                        val impressoraCmb = impressora?.let { Printer(it.name) }
                        val printer = impressoraCmb ?: pacote.impressora
                        PrintUtil.printText(printer, pacote.text)
                      }
            }
          }
        }
      }
      gridNota = grid(NotaLabelVo::class) {
        isExpanded = true
        setSizeFull()
        this.removeAllColumns()
        this.setSelectionMode(SelectionMode.MULTI)

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

  override var listaNota: List<NotaLabelVo>
    get() = gridNota.selectedItems.toList()
    set(value) {
      gridNota.setItems(value)
      value.forEach { item -> gridNota.select(item) }
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
  override var impressora: PrinterInfo?
    get() = cmbImpressora.value
    set(value) {
      cmbImpressora.value = value
    }
}

