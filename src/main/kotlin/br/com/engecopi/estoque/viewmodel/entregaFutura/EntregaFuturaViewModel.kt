package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.ViewCodBarCliente
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.utils.mid

class EntregaFututaViewModel(view: IEntregaFututaView):
  NotaViewModel<EntregaFututaVo, IEntregaFututaView>(view, SAIDA, ENTREGUE,
                                                       CONFERIDA, "") {
  override fun newBean(): EntregaFututaVo {
    return EntregaFututaVo()
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
      .let {q ->
        if(usuarioDefault.isEstoqueExpedicao) q.localizacao.startsWith(abreviacaoDefault)
        else q
      }
  }

  override fun createVo() = EntregaFututaVo()

  fun processaKey(key: String) = execList {
    val itens = findItens(key)
    if(itens.isEmpty()) throw EViewModel("Produto não encontrado")
    itens.forEach {item ->
      val codigoProduto = item.produto?.codigo?.trim() ?: ""
      if(item.status == ENTREGUE || item.status == ENT_LOJA) showWarning("Produto $codigoProduto já foi entregue")
      else if(item.status == INCLUIDA) showWarning("Produto $codigoProduto ainda não foi conferido")
      else if(item.status == CONFERIDA) {
        item.status = ENTREGUE
        item.save()
      }
    }
    return@execList itens
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
    if(loja != RegistryUserInfo.lojaDefault.numero) return emptyList()
    return Nota.findSaida(numero)
      ?.itensNota()
      .orEmpty()
  }

  fun notasConferidas(): List<EntregaFututaVo> {
    return ItemNota.where()
      .status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

class EntregaFututaVo: NotaVo(SAIDA, "")

interface IEntregaFututaView: INotaView