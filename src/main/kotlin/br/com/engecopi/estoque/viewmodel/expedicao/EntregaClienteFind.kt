package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.ViewCodBarCliente
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EViewModelWarning
import br.com.engecopi.utils.mid

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
    val loja = if(key.isNotEmpty()) key.mid(0, 1).toIntOrNull() ?: return emptyList() else return emptyList()
    val numero = if(key.length > 1) key.mid(1) else return emptyList()
    if(loja != RegistryUserInfo.lojaDeposito.numero) return emptyList()
    return Nota.findSaida(RegistryUserInfo.lojaDeposito, numero)
      ?.itensNota()
      .orEmpty()
  }
}