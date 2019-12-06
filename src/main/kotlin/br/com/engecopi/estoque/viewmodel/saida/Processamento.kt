package br.com.engecopi.estoque.viewmodel.saida

import br.com.engecopi.estoque.model.LancamentoOrigem.DEPOSITO
import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Nota.Find
import br.com.engecopi.estoque.model.NotaItens
import br.com.engecopi.estoque.model.NotaItens.Companion
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.utils.mid

internal class Processamento(private val view: ISaidaView) {
  fun processaKey(key: String): NotaItens {
    val notaItens = processaKeyNumeroNota(key)
    val ret = if(notaItens.vazio) processaKeyBarcodeConferencia(key)
    else notaItens
    view.updateView()
    return ret
  }
  
  private fun processaKeyNumero(loja: Loja, numeroNota: String): NotaItens {
    val notasSaci =
      Nota.findNotaSaidaSaci(loja, numeroNota)
        .filter {loc ->
          loc.localizacaoes()
            .any {it.abreviacao == RegistryUserInfo.abreviacaoDefault}
        }
    val notaSaci = notasSaci.firstOrNull() ?: return NotaItens.VAZIO
    return if(RegistryUserInfo.usuarioDefault.isTipoCompativel(notaSaci.tipoNota())) {
      Nota.createNotaItens(notasSaci)
        .apply {
          this.nota?.lancamentoOrigem = DEPOSITO
        }
    }
    else NotaItens.VAZIO
  }
  
  private fun processaKeyBarcodeConferencia(key: String): NotaItens {
    val item = ViewCodBarConferencia.findNota(key)
               ?: return NotaItens.VAZIO
    if(item.abreviacao != RegistryUserInfo.abreviacaoDefault) throw EViewModel(
      "Esta nota não pertence ao cd ${RegistryUserInfo.abreviacaoDefault}")
    val nota = Nota.findSaida(item.storeno, item.numero)
               ?: return NotaItens.VAZIO
    if(nota.lancamentoOrigem != EXPEDICAO) throw EViewModel(
      "Essa nota não foi lançada pela a expedição")
    return NotaItens(nota, nota.itensNota())
  }
  
  private fun processaKeyNumeroNota(key: String): NotaItens {
    val storeno = if(key.isNotEmpty()) key.mid(0, 1).toIntOrNull() ?: return NotaItens.VAZIO
    else return NotaItens.VAZIO
    val loja = Loja.findLoja(storeno)
               ?: return NotaItens.VAZIO
    val numero = if(key.length > 1) key.mid(1) else return NotaItens.VAZIO
    val notaItem = processaKeyNumero(loja, numero)
    return if(notaItem.nota?.tipoNota == VENDAF || loja.numero == RegistryUserInfo.lojaDeposito.numero) {
      notaItem
    }
    else NotaItens.VAZIO
  }
}