package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.query.QItemNota

class NFVendaFuturaPrint() {
  fun imprimir(nota: Nota?): String {
    nota ?: return ""
    val id = nota.id
    val notaRef = Nota.byId(id) ?: return ""
    val listaItens = notaRef.itensNota()
    return imprimeItens(listaItens)
  }
  
  private fun imprimeItens(itens: List<ItemNota>): String {
    val etiquetas =
      Etiqueta.findByStatus(INCLUIDA)
        .filter {etiqueta ->
          etiqueta.titulo.contains("ETDEP")
        }
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      itens.map {imprimir(it, etiqueta)}
        .distinct()
        .joinToString(separator = "\n")
    }
  }
  
  fun imprimeTudo(): String {
    val etiquetas =
      Etiqueta.findByStatus(INCLUIDA)
        .filter {etiqueta ->
          etiqueta.titulo.contains("ETDEP")
        }
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
  
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    if(RegistryUserInfo.usuarioDefault.isEstoqueVendaFutura) return ""
    itemNota ?: return ""
    if(!etiqueta.imprimivel()) return ""
    val print = itemNota.printEtiqueta()
    itemNota.let {
      it.refresh()
      it.impresso = true
      it.update()
    }
    return print.print(etiqueta.template)
  }
}