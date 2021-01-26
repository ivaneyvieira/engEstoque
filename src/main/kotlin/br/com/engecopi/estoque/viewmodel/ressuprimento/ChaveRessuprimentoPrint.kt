package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.query.QItemNota

class ChaveRessuprimentoPrint() {
  fun imprimir(nota: Nota?): List<EtiquetaRessuprimento> {
    nota ?: return return emptyList()
    val id = nota.id
    val notaRef = Nota.byId(id) ?: return emptyList()
    val listaItens = notaRef.itensNota()
    return imprimeItens(listaItens)
  }
  
  private fun imprimeItens(itens: List<ItemNota>): List<EtiquetaRessuprimento> {
    val etiquetas =
      Etiqueta.findByStatus(INCLUIDA, "ETDEP")
    val prints = etiquetas.flatMap {etiqueta ->
      itens.mapNotNull {item ->
        val text = imprimir(item, etiqueta)
        val nota = item.nota ?: return@mapNotNull null
        val abreviacao = item.abreviacao ?: return@mapNotNull null
        EtiquetaRessuprimento(nota, abreviacao, text)
      }
    }
    return prints.distinctBy {it.text}
  }
  
  fun imprimeTudo(): List<EtiquetaRessuprimento> {
    val itens =
      QItemNota().impresso.eq(false)
        .status.eq(INCLUIDA)
        .findList()
    return imprimeItens(itens)
  }
  
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
}

data class EtiquetaRessuprimento(val nota: Nota, val abreviacao: Abreviacao, val text: String)