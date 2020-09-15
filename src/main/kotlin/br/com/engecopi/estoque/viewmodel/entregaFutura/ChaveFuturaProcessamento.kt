package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.ENTREGA_F
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.framework.viewmodel.EViewModelError
import java.time.LocalDate
import java.time.LocalTime

class ChaveFuturaProcessamento() {
  fun processaKey(notasSaci: List<ItemChaveFutura>): Nota {
    if(notasSaci.all {
        it.isSave()
      }) throw EViewModelError("Todos os itens dessa nota já estão lançados")
    return if(notasSaci.isNotEmpty()) processaNota(notasSaci)
    else throw EChaveNaoEncontrada()
  }
  
  private fun processaNota(itensChaveFutura: List<ItemChaveFutura>): Nota {
    val notaDoSaci =
      itensChaveFutura.firstOrNull()
        ?.notaProdutoSaci
    notaDoSaci?.storeno ?: throw EViewModelError("Nota não encontrada")
    val nota: Nota? =
      Nota.createNota(notaDoSaci)
        ?.let {
          if(it.existe()) Nota.findSaida(it.loja, it.numero)
          else {
            it.sequencia = Nota.maxSequencia() + 1
            it.usuario = usuarioDefault
            it.lancamentoOrigem = ENTREGA_F
            it.save()
            it
          }
        }
    nota ?: throw EViewModelError("Nota não encontrada")
    val itens = itensChaveFutura.mapNotNull {itemVendaFutura ->
      val notaSaci = itemVendaFutura.notaProdutoSaci
      val item = ItemNota.find(notaSaci) ?: ItemNota.createItemNota(notaSaci, nota, itemVendaFutura.abrevicao)
      return@mapNotNull item?.apply {
        this.status = INCLUIDA
        this.impresso = false
        this.usuario = usuarioDefault
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