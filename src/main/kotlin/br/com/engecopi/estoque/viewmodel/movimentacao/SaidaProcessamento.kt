package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.framework.viewmodel.IView
import java.time.LocalDate
import java.time.LocalTime

class SaidaProcessamento(val view: IView) {
  fun confirmaProdutos(itens: List<ProdutoNotaVo>, situacao: StatusNota): List<ItemNota> {
    itens.firstOrNull()?.value?.nota?.save()
    val listMultable = mutableListOf<ItemNota>()
    itens.forEach { produtoVO ->
      produtoVO.value?.run {
        if (this.id != 0L) refresh()

        this.status = situacao
        this.impresso = false
        this.usuario = RegistryUserInfo.usuarioDefault
        this.data = LocalDate.now()
        this.hora = LocalTime.now()
        this.localizacao = produtoVO.localizacao?.localizacao ?: ""
        if (this.quantidade >= produtoVO.quantidade) {
          this.quantidade = produtoVO.quantidade
          this.save()
          produtoVO.isSave = true
          this.recalculaSaldos()
          listMultable.add(this)
        }
        else view.showWarning("A quantidade do produto ${produto?.codigo} não pode ser maior que $quantidade")
      }
    }
    return listMultable
  }
}