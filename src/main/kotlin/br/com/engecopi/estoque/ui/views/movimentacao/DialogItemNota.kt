package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.viewmodel.movimentacao.ProdutoNotaVo
import br.com.engecopi.estoque.viewmodel.movimentacao.ValidadeProduto
import br.com.engecopi.framework.ui.view.default
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.shared.ui.datefield.DateResolution
import com.vaadin.ui.ComboBox
import com.vaadin.ui.DateField
import com.vaadin.ui.Notification
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme
import java.time.LocalDate

class DialogItemNota(val nota: Nota?, private val produtoVo: ProdutoNotaVo, val salvaProduto: () -> Unit = {}) :
  Window("Produto") {
  private var cmbLocalizacao: ComboBox<LocProduto>? = null
  private var edtDataFabricacao: DateField? = null

  init {
    verticalLayout {
      formLayout {
        textField("Código") {
          this.value = produtoVo.codigo.trim()
          this.isReadOnly = true
        }
        textField("Descrição") {
          this.value = produtoVo.descricaoProduto.trim()
          this.isReadOnly = true
        }
        edtDataFabricacao = dateField("Fabricação") {
          val meses = produtoVo.produto.mesesVencimento ?: 0
          this.isVisible = meses > 0
          placeholder = "mm/aaaa"
          this.dateFormat = "MM/yyyy"
          this.resolution = DateResolution.MONTH
          this.value = produtoVo.dataFabricacao
        }
        cmbLocalizacao = comboBox<LocProduto>("Localização") {
          default { localizacao ->
            localizacao.localizacao
          }
          isTextInputAllowed = true

          val produto = produtoVo.produto
          val locSulfixos = produto.localizacoes(RegistryUserInfo.abreviacaoDefault).map { LocProduto(it) }
          this.setItems(locSulfixos)
          this.setItemCaptionGenerator { it.localizacao }
          this.value = produtoVo.localizacao
        }
      }
      horizontalLayout {
        button("Salva") {
          this.styleName = ValoTheme.BUTTON_PRIMARY
          onLeftClick {
            val msg = validaProduto()
            if (msg.isOk()) {
              produtoVo.localizacao = cmbLocalizacao?.value
              produtoVo.dataFabricacao = edtDataFabricacao?.value
              produtoVo.selecionado = true
              salvaProduto()
              this@DialogItemNota.close()
            } else {
              Notification.show(msg.msgErro(), Notification.Type.ERROR_MESSAGE)
            }
          }
        }
        button("Cancelar") {
          onLeftClick {
            this@DialogItemNota.close()
          }
        }
        edtDataFabricacao?.focus()
      }
    }
  }

  private fun validaProduto(): ValidadeProduto {
    val produto = produtoVo.produto
    val mesesValidade = produto.mesesVencimento ?: 0

    return ValidadeProduto.erroMesFabricacao(
      produto = produto.codigo,
      dataFabricacao = edtDataFabricacao?.value,
      dataEntrada = nota?.data ?: LocalDate.now(),
      mesesValidade = mesesValidade,
    )
  }
}