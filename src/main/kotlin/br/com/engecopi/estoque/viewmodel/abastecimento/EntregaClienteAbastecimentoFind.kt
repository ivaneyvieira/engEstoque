package br.com.engecopi.estoque.viewmodel.abastecimento

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
import br.com.engecopi.framework.viewmodel.IView

class EntregaClienteAbastecimentoFind(val view: IView) {
  fun findKey(key: String): List<ItemNota> {
    val itens = (findItensNumero(key) + findItensBarcode(key)).filter {it.status == CONFERIDA}
    if(itens.isEmpty()) throw EViewModelError("Produto não encontrado")
    itens.forEach {item ->
      val codigoProduto = item.produto?.codigo?.trim() ?: ""
      when(item.status) {
        ENTREGUE, ENT_LOJA -> view.showWarning("Produto $codigoProduto já foi entregue")
        INCLUIDA           -> view.showWarning("Produto $codigoProduto ainda não foi conferido")
        CONFERIDA          -> {
          item.status = ENTREGUE
          item.save()
        }
        else               -> view.showWarning("Operação inválida")
      }
    }
    return itens
  }
  
  private fun findItensBarcode(key: String): List<ItemNota> {
    return ItemNota.findItensBarcodeCliente(key)
  }
  
  private fun findItensNumero(key: String): List<ItemNota> {
    val itemUnico = processaKeyBarcodeCliente(key)
    return if(itemUnico.isEmpty()) {
      val itensConferencia = ViewCodBarConferencia.findKeyItemNota(key)
      if(itensConferencia.isEmpty()) ViewCodBarCliente.findKeyItemNota(key, CONFERIDA)
      else itensConferencia
    }
    else itemUnico
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