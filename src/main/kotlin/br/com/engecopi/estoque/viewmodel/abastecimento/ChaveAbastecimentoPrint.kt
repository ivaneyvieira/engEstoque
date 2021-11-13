package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.query.QItemNota

class ChaveAbastecimentoPrint() {
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    itemNota.let {
      it.refresh()
      it.impresso = true
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
 
      itensAbreviacao.mapNotNull {(abreviacao, itens) ->
        if(abreviacao == null) return@mapNotNull null
        val printer = abreviacao.printer
        val text = imprimeItens(INCLUIDA, itens)
        PacoteImpressao(printer, text)
      }
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