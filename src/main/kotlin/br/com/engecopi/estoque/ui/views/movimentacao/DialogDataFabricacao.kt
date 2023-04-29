package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.viewmodel.movimentacao.ProdutoNotaVo
import br.com.engecopi.estoque.viewmodel.movimentacao.ValidadeProduto
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.shared.ui.datefield.DateResolution
import com.vaadin.ui.DateField
import com.vaadin.ui.Notification
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme
import java.time.LocalDate

class DialogDataFabricacao(val nota: Nota?, private val produtoVo: ProdutoNotaVo, val salvaProduto: () -> Unit = {}) :
  Window("Data de Fabricação") {
  private lateinit var edtDataFabricacao: DateField

  init {
    formLayout {
      edtDataFabricacao = dateField("Fabricação") {
        val meses = produtoVo.produto.mesesVencimento ?: 0
        this.isVisible = meses > 0
        placeholder = "mm/aaaa"
        this.dateFormat = "MM/yyyy"
        this.resolution = DateResolution.MONTH
        this.value = produtoVo.dataFabricacao
      }
    }
    horizontalLayout {
      button("Salva") {
        this.styleName = ValoTheme.BUTTON_PRIMARY
        onLeftClick {
          val msg = validaProduto()
          if (msg.isOk()) {
            produtoVo.dataFabricacao = edtDataFabricacao.value
            produtoVo.selecionado = true
            salvaProduto()
            this@DialogDataFabricacao.close()
          } else {
            Notification.show(msg.msgErro(), Notification.Type.ERROR_MESSAGE)
          }
        }
      }
      button("Cancelar") {
        onLeftClick {
          this@DialogDataFabricacao.close()
        }
      }
      edtDataFabricacao.focus()
    }
  }

  private fun validaProduto(): ValidadeProduto {
    val produto = produtoVo.produto
    val mesesValidade = produto.mesesVencimento ?: 0
    return ValidadeProduto.erroMesFabricacao(
      produto = produto.codigo,
      dataFabricacao = edtDataFabricacao.value,
      dataEntrada = nota?.data ?: LocalDate.now(),
      mesesValidade = mesesValidade,
    )
  }
}