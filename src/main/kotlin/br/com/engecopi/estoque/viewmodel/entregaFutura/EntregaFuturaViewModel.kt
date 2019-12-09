package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.dtos.TransferenciaAutomatica
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.utils.mid

class EntregaFututaViewModel(view: IEntregaFututaView):
  NotaViewModel<EntregaFututaVo, IEntregaFututaView>(view, SAIDA, ENTREGUE, CONFERIDA, "") {
  private val findNota = FindNota(view)
  
  override fun newBean(): EntregaFututaVo {
    return EntregaFututaVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this.nota.tipoNota.eq(VENDAF)
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(CONFERIDA)
      .nota.usuario.isNotNull.nota.sequencia.ne(0)
      .let {q ->
        if(usuarioDefault.isEstoqueExpedicao) {
          q.localizacao.startsWith(abreviacaoDefault)
        }
        else q
      }
  }
  
  override fun createVo() = EntregaFututaVo()
  
  fun findKey(key: String) = execList {findNota.findKey(key)}
  
  fun notasConferidas(): List<EntregaFututaVo> {
    return QItemNota().status.eq(CONFERIDA)
      .findList()
      .map {it.toVO()}
  }
}

class EntregaFututaVo: NotaVo(SAIDA, "")

interface IEntregaFututaView: INotaView

class FindNota(private val view: IEntregaFututaView) {
  fun findKey(key: String): List<ItemNota> {
    val itens = findItensNotaTransferencia(key)
    if(itens.isEmpty()) {
      throw EViewModel("Produto não encontrado")
    }
    itens.forEach {item ->
      val codigoProduto = item.produto?.codigo?.trim() ?: ""
      when(item.status) {
        ENTREGUE, ENT_LOJA -> {
          view.showWarning("Produto $codigoProduto já foi entregue")
        }
        INCLUIDA           -> {
          view.showWarning("Produto $codigoProduto ainda não foi conferido")
        }
        CONFERIDA          -> {
          item.status = ENTREGUE
          item.save()
          item.nota?.let {nota ->
            if(nota.tipoNota == VENDAF) {
              nota.numeroEntrega = nota.numeroEntrega()
              nota.save()
            }
          }
        }
        else               -> {
          view.showWarning("Operação inválida")
        }
      }
    }
    view.updateView()
    return itens
  }
  
  private fun findItensNotaTransferencia(key: String): List<ItemNota> {
    val notaTransferencia =
      Nota.findNotaSaidaKey(key)
        .firstOrNull()
    return if(notaTransferencia == null) {
      val storeno = key.mid(0, 1).toIntOrNull() ?: return emptyList()
      val numero = key.mid(1)
      findItensNotaTransferencia(storeno, numero)
    }
    else {
      val lojaTransferencia = notaTransferencia.storeno ?: return emptyList()
      val numeroSerieTransferencia = notaTransferencia.numeroSerie()
      findItensNotaTransferencia(lojaTransferencia, numeroSerieTransferencia)
    }
  }
  
  private fun findItensNotaTransferencia(storeno: Int, numero: String): List<ItemNota> {
    val notaFutura = TransferenciaAutomatica.notaFutura(storeno, numero) ?: return emptyList()
    val lojaFaturamento = notaFutura.storenoFat
    val numeroFaturamento = notaFutura.nffat
    return ItemNota.find(lojaFaturamento, numeroFaturamento)
  }
}