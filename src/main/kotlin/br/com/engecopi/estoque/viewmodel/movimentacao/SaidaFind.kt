package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.LancamentoOrigem.DEPOSITO
import br.com.engecopi.estoque.model.LancamentoOrigem.ENTREGA_F
import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.LancamentoOrigem.RESSUPRI
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaItens
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.TipoNota.CHAVE_SAIDA
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_R
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.framework.viewmodel.EViewModelError

class SaidaFind() {
  fun findByBarcodeProduto(barcode: String?): List<Produto> {
    return if(barcode.isNullOrBlank()) emptyList()
    else Produto.findBarcode(barcode)
  }
  
  fun findByKey(key: String): NotaItens {
    val notaItens = processaKeyNumeroNota(key)
    return if(notaItens.vazio) processaKeyBarcodeConferencia(key)
    else notaItens
  }
  
  private fun processaKeyNumero(loja: Loja?, numeroNota: String): NotaItens {
    loja ?: return NotaItens.erro("Loja inválida")
    val notaFutura = Nota.findSaida(loja, numeroNota)
    return if(notaFutura == null) {
      processaNotaSaci(loja, numeroNota)
    }
    else {
      if(notaFutura.lancamentoOrigem == ENTREGA_F) {
        NotaItens(notaFutura, notaFutura.itensNota())
      }
      else processaNotaSaci(loja, numeroNota)
    }
  }
  
  private fun processaNotaSaci(loja: Loja, numeroNota: String): NotaItens {
    val notasSaci =
      Nota.findNotaSaidaSaci(loja, numeroNota)
        .filter {loc ->
          loc.localizacaoes()
            .any {it.abreviacao == abreviacaoDefault}
        }
    val notaSaci = notasSaci.firstOrNull()
                   ?: return NotaItens.erro("Nota inválida")
    return if(usuarioDefault.isTipoCompativel(notaSaci.tipoNota())) {
      Nota.createNotaItens(notasSaci)
        .apply {
          this.nota?.lancamentoOrigem = DEPOSITO
        }
    }
    else NotaItens.erro("O usuário não possui permissão para trabalha com nota do tipo ${notaSaci.tipoNota()}")
  }
  
  private fun processaKeyBarcodeConferencia(key: String): NotaItens {
    if(!usuarioDefault.isTipoCompativel(CHAVE_SAIDA))
      return NotaItens.erro("O usuário não possui permissão para usar a chave")
    val item = ViewCodBarConferencia.findNota(key)
               ?: return NotaItens.erro("Nota não encontrada")
    if(item.abreviacao != abreviacaoDefault) {
      throw EViewModelError("Esta nota não pertence ao cd ${abreviacaoDefault}")
    }
    val nota = Nota.findSaida(item.storeno, item.numero) ?: return NotaItens.erro("Nota não encontrada")
    if(nota.lancamentoOrigem !in listOf(EXPEDICAO, ENTREGA_F, RESSUPRI)) {
      throw EViewModelError("Essa nota não foi lançada pela a expedição ou pela entrega futura")
    }
    return NotaItens(nota, nota.itensNota())
  }
  
  private fun processaKeyNumeroNota(key: String): NotaItens {
    val keyNota = KeyNota(key)
    val notaItem = processaKeyNumero(keyNota.loja, keyNota.numero)
    return if(notaItem.nota?.tipoNota in listOf(VENDAF, PEDIDO_R) || keyNota.storeno == lojaDeposito.numero) {
      notaItem
    }
    else NotaItens.erro("A nota pertence a a loja ${lojaDeposito.numero} e não é nota de entrega futura")
  }
}
