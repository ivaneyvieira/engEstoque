package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.viewmodel.EChaveNaoEncontrada
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.saci.beans.NotaProdutoSaci
import java.time.LocalDate
import java.time.LocalTime

class ChaveExpedicaoProcessamento {
  fun processaKey(notasSaci: List<ItemExpedicao>): Nota {
    if (notasSaci.all { it.isSave() }) throw EViewModelError("Todos os itens dessa nota já estão lançados")
    return if (notasSaci.isNotEmpty()) processaNota(notasSaci)
    else throw EChaveNaoEncontrada()
  }

  private fun processaNota(itensExpedicao: List<ItemExpedicao>): Nota {
    val loja = lojaDeposito.numero
    val notaDoSaci = itensExpedicao.firstOrNull()?.notaProdutoSaci
    val lojaSaci = notaDoSaci?.storeno ?: throw EViewModelError("Nota não encontrada")
    if (loja != lojaSaci) throw EViewModelError("Esta nota pertence a loja $lojaSaci")
    val nota: Nota? = createNota(notaDoSaci)
    nota ?: throw EViewModelError("Nota não encontrada")
    val itens = itensExpedicao.mapNotNull { itemExpedicao ->
      val notaSaci = itemExpedicao.notaProdutoSaci
      val item = ItemNota.find(notaSaci) ?: ItemNota.createItemNota(notaSaci, nota, itemExpedicao.abrevicao)

      return@mapNotNull item?.apply {
        this.status = if (abreviacao?.expedicao == true) CONFERIDA else INCLUIDA
        this.impresso = false
        this.usuario = RegistryUserInfo.usuarioDefault
        this.data = LocalDate.now()
        this.hora = LocalTime.now()
        this.save()
        if (this.status == CONFERIDA) this.recalculaSaldos()
      }
    }

    if (itens.isEmpty()) throw EViewModelError("Essa nota não possui itens com localização")

    return nota
  }

  private fun createNota(notaDoSaci: NotaProdutoSaci?): Nota? {
    return Nota.createNota(notaDoSaci)?.apply {
              if (this.existe()) Nota.findSaida(this.loja, this.numero)
              else {
                this.sequencia = Nota.maxSequencia() + 1
                this.usuario = usuarioDefault
                this.lancamentoOrigem = EXPEDICAO
                this.save()
              }
            }
  }
}