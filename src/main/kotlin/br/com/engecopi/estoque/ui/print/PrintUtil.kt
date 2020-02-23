package br.com.engecopi.estoque.ui.print

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.ENTREGA_F
import br.com.engecopi.estoque.model.LancamentoOrigem.RESSUPRI
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoUsuario.EXPEDICAO
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.framework.model.AppPrinter
import br.com.engecopi.framework.model.ECupsPrinter
import br.com.engecopi.framework.ui.view.MessageDialog
import br.com.engecopi.saci.QuerySaci
import br.com.engecopi.utils.ZPLPreview
import com.vaadin.server.Page
import com.vaadin.shared.Position.TOP_CENTER
import com.vaadin.ui.Notification
import com.vaadin.ui.Notification.Type.ERROR_MESSAGE
import com.vaadin.ui.themes.ValoTheme

object PrintUtil {
  fun imprimeNotaConcluida(nota: Nota?) {
    nota ?: return
    val impressoraNota = when(nota.lancamentoOrigem) {
      EXPEDICAO -> Printer("EXP4")
      ENTREGA_F -> Printer("ENTREGA")
      RESSUPRI  -> {
        val printerNota = nota.itensNota().firstOrNull()?.abreviacao?.printer ?: return
        printerNota
      }
      else      -> Printer("")
    }
    val textNota = this.imprimirNota(nota)
    printText(impressoraNota, textNota)
  }
  
  fun printText(impressora: Printer?, text: String?) {
    impressora ?: return
    if(!text.isNullOrBlank()) {
      when {
        QuerySaci.test -> {
          val image = ZPLPreview.createPdf(text, "4x2")
          if(image != null) showImage("Preview ${impressora.nome}", image)
        }
        else           -> try {
          AppPrinter.printCups(impressora.nome, text)
        } catch(e: ECupsPrinter) {
          Notification("Erro",
                       "\n" + e.message,
                       ERROR_MESSAGE).apply {
            styleName += " " + ValoTheme.NOTIFICATION_CLOSABLE
            position = TOP_CENTER
            show(Page.getCurrent())
          }
        }
      }
    }
  }
  
  private fun imprimirNotaConferida(itens: List<ItemNota>): String {
    val etiquetas = Etiqueta.findByStatus(CONFERIDA, "ETENT")
    return etiquetas.joinToString(separator = "\n") {etiqueta ->
      imprimir(itens, etiqueta)
    }
  }
  
  private fun imprimirNota(nota: Nota): String {
    val itens = nota.itensNota()
    val itensIncluidos = itens.filter {it.status == INCLUIDA}
    val itensConferidos = itens.filter {it.status == CONFERIDA}
    return if(itensIncluidos.isEmpty()) {
      imprimirNotaConferida(itensConferidos)
    }
    else ""
  }
  
  private fun imprimir(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.map {imprimir(it, etiqueta)}
      .distinct()
      .joinToString(separator = "\n")
  }
  
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    if(!usuarioDefault.admin) itemNota.let {item ->
      item.refresh()
      item.impresso = true
      item.update()
    }
  
    return print.print(etiqueta.template)
  }
  
  private fun showImage(title: String, image: ByteArray) {
    MessageDialog.image(title, image)
  }
}

