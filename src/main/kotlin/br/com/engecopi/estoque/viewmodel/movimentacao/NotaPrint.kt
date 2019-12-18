package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.query.QItemNota

class NotaPrint() {
  fun imprimir(itemNota: ItemNota?, notaCompleta: Boolean, groupByHour: Boolean, statusImpressao: StatusNota): String {
    itemNota ?: return ""
    return if(notaCompleta) {
      val itens =
        QItemNota().nota.eq(itemNota.nota)
          .status.eq(itemNota.status)
          .let {
            if(groupByHour) it.hora.eq(itemNota.hora) else it
          }
          .order()
          .nota.loja.numero.asc()
          .nota.numero.asc()
          .findList()
      imprimirItens(itens, statusImpressao)
    }
    else imprimirItens(listOf(itemNota), statusImpressao)
  }
  
  fun imprimir(statusImpressao: StatusNota): String {
    val itens = QItemNota().let {q ->
      if(RegistryUserInfo.usuarioDefault.admin) q else q.impresso.eq(false)
    }
      .status.eq(statusImpressao)
      .order()
      .nota.loja.numero.asc()
      .nota.numero.asc()
      .findList()
    return imprimirItens(itens, statusImpressao)
  }
  
  fun imprimirItens(itens: List<ItemNota>, statusImpressao: StatusNota): String {
    val etiquetas =
      Etiqueta.findByStatus(statusImpressao)
        .filter {
          !it.titulo.contains("ETENT")
        }
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      imprimir(itens, etiqueta)
    }
  }
  
  fun imprimirNota(itens: List<ItemNota>, statusImpressao: StatusNota): String {
    val etiquetas =
      Etiqueta.findByStatus(statusImpressao)
        .filter {
          it.titulo.contains("ETENT")
        }
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      imprimir(itens, etiqueta)
    }
  }
  
  private fun imprimir(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.filter {it.abreviacao?.abreviacao == abreviacaoDefault}
      .map {imprimir(it, etiqueta)}
      .distinct()
      .joinToString(separator = "\n")
  }
  
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    if(!RegistryUserInfo.usuarioDefault.admin) itemNota.let {
      it.refresh()
      it.impresso = true
      it.update()
    }
    
    return print.print(etiqueta.template)
  }
}