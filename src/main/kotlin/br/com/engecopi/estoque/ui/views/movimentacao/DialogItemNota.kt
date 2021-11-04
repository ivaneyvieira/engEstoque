package br.com.engecopi.estoque.ui.views.movimentacao

import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.viewmodel.movimentacao.ProdutoNotaVo
import br.com.engecopi.framework.ui.view.default
import com.github.mvysny.karibudsl.v8.*
import com.vaadin.shared.ui.datefield.DateResolution
import com.vaadin.ui.ComboBox
import com.vaadin.ui.DateField
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme

class DialogItemNota(private val produtoVo: ProdutoNotaVo, val salvaProduto: () -> Unit = {}) : Window("Produto") {
  private lateinit var cmbLocalizacao: ComboBox<LocProduto>
  private lateinit var edtDataValidade: DateField

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
        edtDataValidade = dateField("Validade") {
          val meses = produtoVo.produto.mesesVencimento ?: 0
          this.isVisible = meses > 0
          placeholder = "mm/aaaa"
          this.dateFormat = "MM/yyyy"
          this.resolution = DateResolution.MONTH
          this.value = produtoVo.dataValidade
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
            produtoVo.localizacao = cmbLocalizacao.value
            produtoVo.dataValidade = edtDataValidade.value
            produtoVo.selecionado = true
            salvaProduto()
            this@DialogItemNota.close()
          }
        }
        button("Cancelar") {
          onLeftClick {
            this@DialogItemNota.close()
          }
        }
        edtDataValidade.focus()
      }
    }
  }
}