package br.com.engecopi.estoque.viewmodel.ressuprimento

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.RESSUPRI
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.saci
import java.time.LocalDate
import java.time.LocalTime

class ChaveRessuprimentoProcessamento(val view: IChaveRessuprimentoView) {
  fun processaKey(notasSaci: List<ItemRessuprimento>): Nota {
    if (notasSaci.all {
        it.isSave()
      }) throw EViewModelError("Todos os itens dessa nota já estão lançados")
    return if (notasSaci.isNotEmpty()) processaNota(notasSaci)
    else throw EChaveNaoEncontrada()
  }

  fun processaKey(key: String) {
    val ordno = key.toIntOrNull() ?: throw EViewModelError("Chave inválida")
    val pedidoItens = saci.findPedidoRessuprimento(ordno).ifEmpty {
      throw EViewModelError("Pedido não encontrado")
    }
    if (pedidoItens.all {
        Produto.findProduto(
          it.prdno, it.grade
        ) == null
      }) throw EViewModelError("Esse pedido não possui produtos cadastrado na localização")
    val pedidoSaci = pedidoItens.firstOrNull()
    val nota = Nota.findSaida(pedidoSaci?.storeno, pedidoSaci?.numeroSerie())
    nota?.let {
      throw EViewModelError("Esse pedido já foi lido")
    }
    Nota.createNota(pedidoSaci)?.let { novaNota ->
      novaNota.lancamentoOrigem = RESSUPRI
      novaNota.sequencia = Nota.maxSequencia() + 1
      novaNota.usuario = usuarioDefault

      novaNota.save()
      pedidoItens.forEach { pedidoItem ->
        val itemNota = ItemNota.find(pedidoItem) ?: ItemNota.createItemNota(
          notaProdutoSaci = pedidoItem, notaPrd = novaNota, abreviacao = pedidoItem.abreviacao ?: ""
        )
        itemNota?.apply {
          this.status = INCLUIDA
          this.save()
        }
      }
    }

    view.updateGrid()
  }

  private fun processaNota(itensVendaFutura: List<ItemRessuprimento>): Nota {
    val notaDoSaci = itensVendaFutura.firstOrNull()?.notaProdutoSaci
    notaDoSaci?.storeno ?: throw EViewModelError("Nota não encontrada")
    val nota: Nota? = Nota.createNota(notaDoSaci)?.let {
      if (it.existe()) Nota.findSaida(it.loja, it.numero)
      else {
        it.sequencia = Nota.maxSequencia() + 1
        it.usuario = usuarioDefault
        it.lancamentoOrigem = RESSUPRI
        it.save()
        it
      }
    }
    nota ?: throw EViewModelError("Nota não encontrada")
    val itens = itensVendaFutura.mapNotNull { itemVendaFutura ->
      val notaSaci = itemVendaFutura.notaProdutoSaci
      val item = ItemNota.find(notaSaci) ?: ItemNota.createItemNota(notaSaci, nota, itemVendaFutura.abrevicao)
      return@mapNotNull item?.apply {
        this.status = INCLUIDA
        this.impresso = false
        this.usuario = usuarioDefault
        this.data = LocalDate.now()
        this.hora = LocalTime.now()
        this.save()
        if (this.status == CONFERIDA) this.recalculaSaldos()
      }
    }

    if (itens.isEmpty()) throw EViewModelError("Essa nota não possui itens com localização")

    return nota
  }
}