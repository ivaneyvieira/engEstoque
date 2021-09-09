package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.StatusNota.*
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.dtos.EntregaFutura
import br.com.engecopi.estoque.model.dtos.PedidoNotaRessuprimento
import br.com.engecopi.estoque.model.dtos.TransferenciaAutomatica
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.IView

class EntregaFuturaFind(val view: IView) {
  fun findKey(key: String): List<ItemNota> {
    val itens = findItensNotaTransferencia(key).filter {
      val tipoNota = it.status
      tipoNota == CONFERIDA
    }
    if (itens.isEmpty()) {
      throw EViewModelError("Produto não encontrado")
    }
    itens.forEach { item ->
      val codigoProduto = item.produto?.codigo?.trim() ?: ""
      when (item.status) {
        ENTREGUE, ENT_LOJA -> {
          view.showWarning("Produto $codigoProduto já foi entregue")
        }
        INCLUIDA           -> {
          view.showWarning("Produto $codigoProduto ainda não foi conferido")
        }
        CONFERIDA          -> {
          item.status = ENTREGUE
          item.save()
          item.nota?.let { nota ->
            if (nota.tipoNota == VENDAF) {
              nota.numeroEntrega = nota.notaBaixa().firstOrNull()?.numero ?: ""
              nota.save()
            }
          }
        }
        else               -> {
          view.showWarning("Operação inválida")
        }
      }
    }
    return itens
  }

  private fun findItensNotaTransferencia(key: String): List<ItemNota> {
    val notaBaixa = Nota.findNotaSaidaKey(key).firstOrNull()
    return if (notaBaixa == null) {
      val keyNota = KeyNota(key)
      findBaixa(keyNota.storeno, keyNota.numero)
    }
           else {
      val lojaBaixa = notaBaixa.storeno ?: return emptyList()
      val numeroBaixa = notaBaixa.numeroSerie()
      findBaixa(lojaBaixa, numeroBaixa)
    } + ItemNota.findItensBarcodeCliente(key)
  }

  private fun findBaixa(storeno: Int?, numero: String): List<ItemNota> {
    storeno ?: return emptyList()
    val notaTransferencia = findItensNotaTransferencia(storeno, numero)
    return notaTransferencia.ifEmpty {
      val notaFutura = findItensNotaFutura(storeno, numero)
      notaFutura
    }.ifEmpty {
      val notaRessuprimento = findItemRessuprimento(storeno, numero)
      notaRessuprimento
    }
  }

  private fun findItemRessuprimento(storeno: Int, numero: String): List<ItemNota> {
    val notaTransferencia = PedidoNotaRessuprimento.pedidoRessuprimento(storeno, numero)
    return notaTransferencia.flatMap {
      ItemNota.find(it.storeno, it.numero)
    }
  }

  private fun findItensNotaTransferencia(storeno: Int, numero: String): List<ItemNota> {
    val notaTransferencia = TransferenciaAutomatica.notaFatura(storeno, numero)
    return notaTransferencia.flatMap {
      ItemNota.find(it.storeno, it.numero)
    }
  }

  private fun findItensNotaFutura(storeno: Int, numero: String): List<ItemNota> {
    val notaFutura = EntregaFutura.notaFatura(storeno, numero)
    return notaFutura.flatMap {
      ItemNota.find(it.storeno, it.numero)
    }
  }

  fun notasConferidas(): MutableList<ItemNota> {
    return QItemNota().status.eq(CONFERIDA).findList()
  }
}