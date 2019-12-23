package br.com.engecopi.estoque.ui.print

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.ENTREGA_F
import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.framework.ui.view.MessageDialog
import br.com.engecopi.saci.QuerySaci
import br.com.engecopi.utils.CupsUtils
import br.com.engecopi.utils.ZPLPreview

object PrintUtil {
  fun imprimeNotaConcluida(nota: Nota?) {
    nota ?: return
    val impressoraNota = when(nota.lancamentoOrigem) {
      EXPEDICAO -> Printer("EXP4")
      ENTREGA_F -> Printer("ENTREGA")
      else      -> Printer("")
    }
    val textNota = imprimirNota(nota)
    printText(impressoraNota, textNota)
  }
  
  fun printText(impressora: Printer, text: String?) {
    if(!text.isNullOrBlank()) {
      when {
        QuerySaci.test -> {
          val image = ZPLPreview.createPdf(text, "4x2")
          if(image != null) showImage("Preview", image)
        }
        else           -> CupsUtils.printCups(impressora.nome, text)
      }
    }
  }
  
  private fun imprimirNota(itens: List<ItemNota>, statusImpressao: StatusNota): String {
    val etiquetas = Etiqueta.findByStatus(statusImpressao, "ETENT")
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      imprimir(itens, etiqueta)
    }
  }
  
  private fun imprimirNota(nota: Nota): String {
    val itens = nota.itensNota()
    val itensIncluidos = itens.filter {it.status == INCLUIDA}
    val itensConferidos = itens.filter {it.status == CONFERIDA}
    return if(itensIncluidos.isEmpty()) {
      imprimirNota(itensConferidos, CONFERIDA)
    }
    else ""
  }
  
  private fun imprimir(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.filter {it.abreviacao?.abreviacao == RegistryUserInfo.abreviacaoDefault}
      .map {imprimir(it, etiqueta)}
      .distinct()
      .joinToString(separator = "\n")
  }
  
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    if(!usuarioDefault.admin) itemNota.let {
      it.refresh()
      it.impresso = true
      it.update()
    }
    
    return print.print(etiqueta.template)
  }
  
  private fun showImage(title: String, image: ByteArray) {
    MessageDialog.image(title, image)
  }
}

