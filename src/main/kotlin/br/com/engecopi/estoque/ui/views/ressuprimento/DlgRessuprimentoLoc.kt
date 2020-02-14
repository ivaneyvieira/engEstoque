package br.com.engecopi.estoque.ui.views.ressuprimento

import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.viewmodel.ressuprimento.ItemRessuprimento
import br.com.engecopi.estoque.viewmodel.ressuprimento.LocalizacaoRessuprimento
import br.com.engecopi.estoque.viewmodel.ressuprimento.PedidoRessuprimentoViewModel
import br.com.engecopi.framework.ui.view.grupo
import br.com.engecopi.framework.ui.view.row
import br.com.engecopi.framework.ui.view.showDialog
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.localDate
import com.github.mvysny.karibudsl.v8.VAlign.Right
import com.github.mvysny.karibudsl.v8.addColumnFor
import com.github.mvysny.karibudsl.v8.align
import com.github.mvysny.karibudsl.v8.alignment
import com.github.mvysny.karibudsl.v8.button
import com.github.mvysny.karibudsl.v8.dateField
import com.github.mvysny.karibudsl.v8.expandRatio
import com.github.mvysny.karibudsl.v8.getAll
import com.github.mvysny.karibudsl.v8.grid
import com.github.mvysny.karibudsl.v8.horizontalLayout
import com.github.mvysny.karibudsl.v8.px
import com.github.mvysny.karibudsl.v8.refresh
import com.github.mvysny.karibudsl.v8.textField
import com.github.mvysny.karibudsl.v8.verticalLayout
import com.github.mvysny.karibudsl.v8.w
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons.CHECK
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.UI
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme

class DlgRessuprimentoLoc(val notaProdutoSaida: List<NotaProdutoSaci>,
                          val viewModel: PedidoRessuprimentoViewModel,
                          val execConfirma: (itens: List<ItemRessuprimento>) -> Unit): Window("Nota de Saída") {
  private lateinit var gridProdutos: Grid<LocalizacaoRessuprimento>
  
  init {
    val nota = notaProdutoSaida.firstOrNull()
    verticalLayout {
      w =
        (UI.getCurrent().page.browserWindowWidth * 0.8).toInt()
          .px
      
      grupo("Nota fiscal de saída") {
        verticalLayout {
          row {
            textField("Nota Fiscal") {
              expandRatio = 2f
              isReadOnly = true
              value = nota?.numero
            }
            textField("Loja") {
              expandRatio = 2f
              isReadOnly = true
              value =
                viewModel.findLoja(nota?.storeno)
                  ?.sigla
            }
            textField("Tipo") {
              expandRatio = 2f
              isReadOnly = true
              value =
                TipoNota.value(nota?.tipo)
                  ?.descricao
            }
            dateField("Data") {
              expandRatio = 1f
              isReadOnly = true
              value = nota?.date?.localDate()
            }
            textField("Rota") {
              expandRatio = 1f
              isReadOnly = true
              value = nota?.rota
            }
          }
        }
      }
      
      grupo("Localizações") {
        row {
          horizontalLayout {
            button("Confirma") {
              addStyleName(ValoTheme.BUTTON_PRIMARY)
              addClickListener {
                val itens =
                  gridProdutos.dataProvider.getAll()
                    .flatMap {loc ->
                      loc.itensRessuprimento.filter {it.selecionado}
                    }
                execConfirma(itens)
                close()
              }
            }
            button("Cancela") {
              alignment = Alignment.BOTTOM_RIGHT
              addClickListener {
                close()
              }
            }
          }
        }
        row {
          gridProdutos = grid(LocalizacaoRessuprimento::class) {
            val itens = notaProdutoSaida
            val abreviacaoItens = itens.groupBy {item ->
              val abreviacao =
                viewModel.abreviacoes(item.prdno, item.grade)
                  .sorted()
              abreviacao
            }
            val abreviacoes =
              abreviacaoItens.keys.flatten()
                .distinct()
                .map {abrev ->
                  val itensRessuprimento =
                    abreviacaoItens.filter {it.key.contains(abrev)}
                      .map {it.value}
                      .flatten()
                      .distinct()
                      .map {notaSaci ->
                        val saldo = viewModel.saldoProduto(notaSaci, abrev)
                        ItemRessuprimento(notaSaci, saldo, abrev)
                      }
                  LocalizacaoRessuprimento(abrev, itensRessuprimento)
                }
                .sortedBy {it.abreviacao}
            
            this.dataProvider = ListDataProvider(abreviacoes)
            removeAllColumns()
            
            setSizeFull()
            addComponentColumn {item ->
              Button().apply {
                this.icon = CHECK
                this.addClickListener {
                  val dlg = DlgRessuprimento(item, viewModel) {
                    gridProdutos.refresh()
                  }
                  dlg.showDialog()
                }
              }
            }.id = "btnPrintItens"
            addColumnFor(LocalizacaoRessuprimento::abreviacao) {
              expandRatio = 1
              caption = "Código"
            }
            addColumnFor(LocalizacaoRessuprimento::countSelecionado) {
              caption = "Selecionados"
              align = Right
            }
          }
        }
      }
    }
  }
}