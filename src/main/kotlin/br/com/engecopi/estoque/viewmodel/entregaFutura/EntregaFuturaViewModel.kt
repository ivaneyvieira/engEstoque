package br.com.engecopi.estoque.viewmodel.entregaFutura

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.movimentacao.INotaView
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaViewModel
import br.com.engecopi.estoque.viewmodel.movimentacao.NotaVo

class EntregaFututaViewModel(view: IEntregaFututaView):
  NotaViewModel<EntregaFututaVo, IEntregaFututaView>(view, SAIDA, ENTREGUE, CONFERIDA, "") {
  private val find = EntregaFuturaFind()
  
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
  
  fun findKey(key: String) = exec {
    find.findKey(key)
      .updateView()
  }
  
  fun notasConferidas() = find.notasConferidas().map {it.toVO()}
}

class EntregaFututaVo: NotaVo(SAIDA, "") {
  val produtoNota = Produto.all()
}

interface IEntregaFututaView: INotaView

