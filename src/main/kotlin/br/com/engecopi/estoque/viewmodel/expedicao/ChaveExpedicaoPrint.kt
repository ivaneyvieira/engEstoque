package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.model.query.QItemNota

class ChaveExpedicaoPrint() {
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    itemNota.let {
      it.refresh()
      it.impresso = !(it.abreviacao?.expedicao ?: false)
      it.update()
    }
    return print.print(etiqueta.template)
  }
  
  fun imprimir(nota: Nota?): List<PacoteImpressao> {
    return if(nota == null) emptyList()
    else {
      val id = nota.id
      val notaRef = Nota.byId(id) ?: return emptyList()
      val listaItens = notaRef.itensNota()
      val itensAbreviacao = listaItens.groupBy {it.abreviacao}
      val impressaoCD: List<PacoteImpressao> = itensAbreviacao.flatMap {entry ->
        val abreviacao = entry.key ?: return@flatMap emptyList<PacoteImpressao>()
        val itensNota = entry.value
        if(abreviacao.expedicao) {
          val text = imprimeItens(CONFERIDA, itensNota)
          val impressoraName = if(abreviacao.printer == Printer.VAZIA) Printer("Localizacao ${abreviacao.abreviacao}")
          else abreviacao.printer
          listOf(PacoteImpressao(impressoraName, text))
        }
        else emptyList()
      }
      val text = imprimeItens(INCLUIDA, listaItens)
      val impressaoEXP = listOf(PacoteImpressao(Printer("EXP4"), text))
      
      impressaoCD + impressaoEXP
    }
  }
  
  private fun imprimeItens(status: StatusNota, itens: List<ItemNota>): String {
    val etiquetas = Etiqueta.findByStatus(status, "ETEXP")
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      itens.map {imprimir(it, etiqueta)}
        .distinct()
        .joinToString(separator = "\n")
    }
  }
  
  fun imprimeTudo(): String {
    val etiquetas = Etiqueta.findByStatus(INCLUIDA, "ETEXP")
    val itens =
      QItemNota().impresso.eq(false)
        .status.eq(INCLUIDA)
        .findList()
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      itens.map {item -> imprimir(item, etiqueta)}
        .distinct()
        .joinToString(separator = "\n")
    }
  }
}