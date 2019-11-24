package br.com.engecopi.estoque.ui.views.notaVendaFutura

import br.com.engecopi.estoque.viewmodel.LocalizacaoVendaFutura
import br.com.engecopi.estoque.viewmodel.entregaFutura.ItemVendaFutura
import br.com.engecopi.estoque.viewmodel.entregaFutura.NFVendaFuturaViewModel
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import com.github.mvysny.karibudsl.v8.VAlign.Right
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.Alignment
import com.vaadin.ui.Grid
import com.vaadin.ui.Grid.SelectionMode.MULTI
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme

class DlgNotaVendaFutura(
  val localizacaoNota: LocalizacaoVendaFutura, val viewModel: NFVendaFuturaViewModel, val update: () -> Unit
                        ): Window("Itens da expedição") {
  private lateinit var gridProdutos: Grid<ItemVendaFutura>
  
  init {
    verticalLayout {
      w = (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
        .px
      
      grupo("Expedição ${localizacaoNota.abreviacao}") {
        row {
          horizontalLayout {
            alignment = Alignment.BOTTOM_LEFT
            button("Confirma") {
              alignment = Alignment.BOTTOM_RIGHT
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                localizacaoNota.itensVendaFutura.forEach {
                  it.selecionado = false
                }
                val itensSelecionado = gridProdutos.selectedItems.toList()
                  .filter {!it.isSave()}
                
                itensSelecionado.forEach {
                  it.selecionado = true
                }
                update()
                close()
              }
            }
            button("Cancela") {
              alignment = Alignment.BOTTOM_LEFT
              addClickListener {
                close()
              }
            }
          }
        }
        row {
          gridProdutos = grid(ItemVendaFutura::class) {
            val itens = localizacaoNota.itensVendaFutura
            
            this.dataProvider = ListDataProvider(itens)
            removeAllColumns()
            val selectionModel = setSelectionMode(MULTI)
            selectionModel.addSelectionListener {select ->
              if(select.isUserOriginated) {
                select.allSelectedItems.forEach {
                  if(it.isSave()) {
                    Notification.show("Não pode ser selecionado. Já está salvo")
                    selectionModel.deselect(it)
                  }
                  else if(it.saldoFinal < 0) {
                    Notification.show("Não pode ser selecionado. Saldo insuficiente.")
                    selectionModel.deselect(it)
                  }
                }
              }
            }
            
            setSizeFull()
            
            addColumnFor(ItemVendaFutura::prdno) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(ItemVendaFutura::nome) {
              expandRatio = 5
              caption = "Descrição"
            }
            addColumnFor(ItemVendaFutura::grade) {
              expandRatio = 1
              caption = "Grade"
            }
            addColumnFor(ItemVendaFutura::saldo) {
              expandRatio = 1
              caption = "Saldo"
              align = Right
            }
            addColumnFor(ItemVendaFutura::quant) {
              expandRatio = 1
              caption = "Qtd Saida"
              align = Right
            }
            addColumnFor(ItemVendaFutura::saldoFinal) {
              expandRatio = 1
              caption = "Saldo Final"
              align = Right
            }
            
            this.setStyleGenerator {
              when {
                it.isSave()       -> "ok"
                it.saldoFinal < 0 -> "error_row"
                else              -> null
              }
            }
          }
          localizacaoNota.itensVendaFutura.forEach {item ->
            if(item.selecionado) gridProdutos.select(item)
            else gridProdutos.deselect(item)
          }
        }
      }
    }
  }
}