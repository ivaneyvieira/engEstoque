package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.dtos.EntregaFutura
import br.com.engecopi.estoque.model.dtos.TransferenciaAutomatica
import br.com.engecopi.framework.viewmodel.EViewModelError

class EntregaFuturaFind(private val view: IEntregaFututaView) {
  fun findKey(key: String): List<ItemNota> {
    val itens = findItensNotaTransferencia(key)
    if(itens.isEmpty()) {
      throw EViewModelError("Produto não encontrado")
    }
    itens.forEach {item ->
      val codigoProduto = item.produto?.codigo?.trim() ?: ""
      when(item.status) {
        ENTREGUE, ENT_LOJA -> {
          view.showWarning("Produto $codigoProduto já foi entregue")
        }
        INCLUIDA           -> {
          view.showWarning("Produto $codigoProduto ainda não foi conferido")
        }
        CONFERIDA          -> {
          item.status = ENTREGUE
          item.save()
          item.nota?.let {nota ->
            if(nota.tipoNota == VENDAF) {
              nota.numeroEntrega = nota.numeroEntrega()
              nota.save()
            }
          }
        }
        else               -> {
          view.showWarning("Operação inválida")
        }
      }
    }
    view.updateView()
    return itens
  }
  
  private fun findItensNotaTransferencia(key: String): List<ItemNota> {
    val notaBaixa =
      Nota.findNotaSaidaKey(key)
        .firstOrNull()
    return if(notaBaixa == null) {
      val keyNota = KeyNota(key)
      findBaixa(keyNota.storeno, keyNota.numeroSerie)
    }
    else {
      val lojaBaixa = notaBaixa.storeno ?: return emptyList()
      val numeroBaixa = notaBaixa.numeroSerie()
      findBaixa(lojaBaixa, numeroBaixa)
    }
  }
  
  private fun findBaixa(storeno: Int?, numero: String): List<ItemNota> {
    storeno ?: return emptyList()
    val notaTransferencia = findItensNotaTransferencia(storeno, numero)
    val notaFutura = findItensNotaFutura(numero)
    return if(notaTransferencia.isEmpty()) notaFutura else notaTransferencia
  }
  
  private fun findItensNotaTransferencia(storeno: Int, numero: String): List<ItemNota> {
    val notaTransferencia = TransferenciaAutomatica.notaFutura(storeno, numero)
    val storenoNota = notaTransferencia?.storenoFat
    val numeroNota = notaTransferencia?.nffat
    return ItemNota.find(storenoNota, numeroNota)
  }
  
  private fun findItensNotaFutura(numero: String): List<ItemNota> {
    val notaFutura = EntregaFutura.notaFutura(numero)
    val storenoNota = notaFutura?.storeno
    val numeroNota = notaFutura?.numero_venda
    return ItemNota.find(storenoNota, numeroNota)
  }
}