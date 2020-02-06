package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.ENTREGA_F
import br.com.engecopi.estoque.model.LancamentoOrigem.RESSUPRI
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.saci
import java.time.LocalDate
import java.time.LocalTime

class RessuprimentoProcessamento(val view: IPedidoRessuprimentoView) {
  fun processaKey(key: String) {
    val ordno = key.toIntOrNull() ?: throw EViewModelError("Chave inválida")
    val pedidoItens =
      saci.findPedidoRessuprimento(ordno)
        .ifEmpty {
          throw EViewModelError("Pedido não encontrado")
        }
    val pedidoSaci = pedidoItens.firstOrNull()
    Nota.findSaida(pedidoSaci?.storeno, pedidoSaci?.numeroSerie())
      ?.let {
        throw EViewModelError("Esse pedido já foi lido")
      }
    Nota.createNota(pedidoSaci)
      ?.let {nota ->
        nota.lancamentoOrigem = RESSUPRI
        nota.sequencia = Nota.maxSequencia() + 1
        nota.usuario = usuarioDefault
        
        nota.save()
        
        pedidoItens.forEach {pedidoItem ->
          ItemNota.find(pedidoItem)
          ?: ItemNota.createItemNota(notaProdutoSaci = pedidoItem,
                                     notaPrd = nota,
                                     abreviacao = pedidoItem.abreviacao ?: "")?.let {item ->
            item.status = INCLUIDA
            item.save()
          }
        }
      }
    
    view.updateGrid()
  }
  
  private fun processaNota(itensVendaFutura: List<ItemRessuprimento>): Nota {
    val notaDoSaci =
      itensVendaFutura.firstOrNull()
        ?.notaProdutoSaci
    notaDoSaci?.storeno ?: throw EViewModelError("Nota não encontrada")
    val nota: Nota? = Nota.createNota(notaDoSaci)
      ?.let {
        if(it.existe()) Nota.findSaida(it.loja, it.numero)
        else {
          it.sequencia = Nota.maxSequencia() + 1
          it.usuario = RegistryUserInfo.usuarioDefault
          it.lancamentoOrigem = ENTREGA_F
          it.save()
          it
        }
      }
    nota ?: throw EViewModelError("Nota não encontrada")
    val itens = itensVendaFutura.mapNotNull {itemVendaFutura ->
      val notaSaci = itemVendaFutura.notaProdutoSaci
      val item = ItemNota.find(notaSaci) ?: ItemNota.createItemNota(notaSaci, nota, itemVendaFutura.abrevicao)
      return@mapNotNull item?.apply {
        this.status = INCLUIDA
        this.impresso = false
        this.usuario = RegistryUserInfo.usuarioDefault
        this.data = LocalDate.now()
        this.hora = LocalTime.now()
        this.save()
        if(this.status == CONFERIDA) this.recalculaSaldos()
      }
    }
    
    if(itens.isEmpty()) throw EViewModelError("Essa nota não possui itens com localização")
    
    return nota
  }
}