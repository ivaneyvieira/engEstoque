package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.DEPOSITO
import br.com.engecopi.estoque.model.LancamentoOrigem.ENTREGA_F
import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaItens
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.EViewModelWarning
import br.com.engecopi.utils.mid
import java.time.LocalDate
import java.time.LocalTime

class SaidaProcessamento() {
  fun processaKey(key: String): NotaItens {
    val notaItens = processaKeyNumeroNota(key)
    return if(notaItens.vazio) processaKeyBarcodeConferencia(key)
    else notaItens
  }
  
  fun confirmaProdutos(itens: List<ProdutoVO>, situacao: StatusNota): List<ItemNota> {
    itens.firstOrNull()
      ?.value?.nota?.save()
    val listMultable = mutableListOf<ItemNota>()
    itens.forEach {produtoVO ->
      produtoVO.value?.run {
        if(this.id != 0L) refresh()
        
        this.status = situacao
        this.impresso = false
        this.usuario = RegistryUserInfo.usuarioDefault
        this.data = LocalDate.now()
        this.hora = LocalTime.now()
        this.localizacao = produtoVO.localizacao?.localizacao ?: ""
        if(this.quantidade >= produtoVO.quantidade) {
          this.quantidade = produtoVO.quantidade
          this.save()
          produtoVO.isSave = true
          this.recalculaSaldos()
          listMultable.add(this)
        }
        else throw EViewModelWarning("A quantidade do produto ${produto?.codigo} não pode ser maior que $quantidade")
      }
    }
    return listMultable
  }
  
  private fun processaKeyNumero(loja: Loja, numeroNota: String): NotaItens {
    val notaFutura = Nota.findSaida(loja, numeroNota)
    return if(notaFutura == null) {
      val notasSaci =
        Nota.findNotaSaidaSaci(loja, numeroNota)
          .filter {loc ->
            loc.localizacaoes()
              .any {it.abreviacao == RegistryUserInfo.abreviacaoDefault}
          }
      val notaSaci = notasSaci.firstOrNull() ?: return NotaItens.VAZIO
      if(RegistryUserInfo.usuarioDefault.isTipoCompativel(notaSaci.tipoNota())) {
        Nota.createNotaItens(notasSaci)
          .apply {
            this.nota?.lancamentoOrigem = DEPOSITO
          }
      }
      else NotaItens.VAZIO
    }
    else {
      if(notaFutura.lancamentoOrigem == ENTREGA_F) NotaItens(notaFutura, notaFutura.itensNota())
      else NotaItens.VAZIO
    }
  }
  
  private fun processaKeyBarcodeConferencia(key: String): NotaItens {
    val item = ViewCodBarConferencia.findNota(key) ?: return NotaItens.VAZIO
    if(item.abreviacao != RegistryUserInfo.abreviacaoDefault) {
      throw EViewModelError("Esta nota não pertence ao cd ${RegistryUserInfo.abreviacaoDefault}")
    }
    val nota = Nota.findSaida(item.storeno, item.numero) ?: return NotaItens.VAZIO
    if(nota.lancamentoOrigem != EXPEDICAO) {
      throw EViewModelError("Essa nota não foi lançada pela a expedição")
    }
    return NotaItens(nota, nota.itensNota())
  }
  
  private fun processaKeyNumeroNota(key: String): NotaItens {
    val storeno = if(key.isNotEmpty()) key.mid(0, 1).toIntOrNull() ?: return NotaItens.VAZIO
    else return NotaItens.VAZIO
    val loja = Loja.findLoja(storeno) ?: return NotaItens.VAZIO
    val numero = if(key.length > 1) key.mid(1) else return NotaItens.VAZIO
    val notaItem = processaKeyNumero(loja, numero)
    return if(notaItem.nota?.tipoNota == VENDAF || loja.numero == RegistryUserInfo.lojaDeposito.numero) {
      notaItem
    }
    else NotaItens.VAZIO
  }
}