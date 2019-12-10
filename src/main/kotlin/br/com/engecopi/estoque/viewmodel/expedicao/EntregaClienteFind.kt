package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.ViewCodBarCliente
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EViewModelWarning

class EntregaClienteFind() {
  fun findKey(key: String): List<ItemNota> {
    val itens = findItens(key)
    if(itens.isEmpty()) throw EViewModelError("Produto não encontrado")
    itens.forEach {item ->
      val codigoProduto = item.produto?.codigo?.trim() ?: ""
      when(item.status) {
        ENTREGUE, ENT_LOJA -> throw EViewModelWarning("Produto $codigoProduto já foi entregue")
        INCLUIDA           -> throw EViewModelWarning("Produto $codigoProduto ainda não foi conferido")
        CONFERIDA          -> {
          item.status = ENTREGUE
          item.save()
        }
        else               -> throw EViewModelWarning("Operação inválida")
      }
    }
    return itens
  }
  
  private fun findItens(key: String): List<ItemNota> {
    val itemUnico = processaKeyBarcodeCliente(key)
    val itens = if(itemUnico.isEmpty()) {
      val itensConferencia = ViewCodBarConferencia.findKeyItemNota(key)
      if(itensConferencia.isEmpty()) ViewCodBarCliente.findKeyItemNota(key, CONFERIDA)
      else itensConferencia
    }
    else itemUnico
    return itens.filter {it.status == CONFERIDA}
  }
  
  private fun processaKeyBarcodeCliente(key: String): List<ItemNota> {
    val keyNota = KeyNota(key)
    val loja = keyNota.storeno
    val numero = keyNota.numero
    if(loja != lojaDeposito.numero) return emptyList()
    return Nota.findSaida(lojaDeposito, numero)
      ?.itensNota()
      .orEmpty()
  }
}