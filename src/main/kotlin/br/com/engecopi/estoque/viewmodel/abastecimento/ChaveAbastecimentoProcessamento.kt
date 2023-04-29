package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.ABASTECI
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.saci.saci
import java.time.LocalDate
import java.time.LocalTime

class ChaveAbastecimentoProcessamento() {
  fun processaKey(notasSaci: List<ItemAbastecimento>): Nota {
    if (notasSaci.all { it.isSave() }) throw EViewModelError("Todos os itens dessa nota já estão lançados")
    return if (notasSaci.isNotEmpty()) processaNota(notasSaci)
    else throw EChaveNaoEncontrada()
  }

  private fun processaNota(itensAbastecimento: List<ItemAbastecimento>): Nota {
    val loja = lojaDeposito.numero
    val notaDoSaci = itensAbastecimento.firstOrNull()?.notaProdutoSaci
    val lojaSaci = notaDoSaci?.storeno ?: throw EViewModelError("Nota não encontrada")
    if (loja != lojaSaci) throw EViewModelError("Esta nota pertence a loja $lojaSaci")
    val nota: Nota? = createNota(notaDoSaci)
    nota ?: throw EViewModelError("Nota não encontrada")
    val itens = itensAbastecimento.mapNotNull { itemAbastecimento ->
      val notaSaci = itemAbastecimento.notaProdutoSaci
      val item = ItemNota.find(notaSaci) ?: ItemNota.createItemNota(notaSaci, nota, itemAbastecimento.abrevicao)

      return@mapNotNull item?.apply {
        this.status = INCLUIDA
        this.impresso = false
        this.usuario = RegistryUserInfo.usuarioDefault
        this.data = LocalDate.now()
        this.hora = LocalTime.now()
        this.save()
      }
    }

    if (itens.isEmpty()) throw EViewModelError("Essa nota não possui itens com localização")

    saci.expiraPedidoVenda(nota.loja?.numero, nota.numero.toIntOrNull())
    return nota
  }

  private fun createNota(notaDoSaci: NotaProdutoSaci?): Nota? {
    return Nota.createNota(notaDoSaci)?.apply {
      if (this.existe()) Nota.findSaida(this.loja, this.numero)
      else {
        this.sequencia = Nota.maxSequencia() + 1
        this.usuario = usuarioDefault
        this.lancamentoOrigem = ABASTECI
        this.save()
      }
    }
  }
}