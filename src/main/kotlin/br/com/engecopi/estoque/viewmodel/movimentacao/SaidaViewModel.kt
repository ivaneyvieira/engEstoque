package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.query.QItemNota

class SaidaViewModel(view: ISaidaView) : NotaViewModel<SaidaVo, ISaidaView>(view, SAIDA, ENTREGUE, CONFERIDA) {
  private val processing = SaidaProcessamento(view)
  private val find = SaidaFind()

  override fun newBean(): SaidaVo {
    return SaidaVo()
  }

  override val query: QItemNota
    get() = super.query.localizacao.startsWith(abreviacaoDefault)

  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this
  }

  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, CONFERIDA)
  }

  override fun add(bean: SaidaVo) { //Não faze nada
  }

  override fun createVo() = SaidaVo()

  fun findByBarcodeProduto(barcode: String?): List<Produto> {
    return find.findByBarcodeProduto(barcode)
  }

  fun findByKey(key: String) = exec {
    find.findByKey(key).apply {
      view.updateView()
    }
  }

  fun confirmaProdutos(itens: List<ProdutoNotaVo>, situacao: StatusNota) = execList {
    processing.confirmaProdutos(itens, situacao).apply {
      view.updateView()
    }
  }
}

class SaidaVo : NotaVo(SAIDA, abreviacaoDefault)

interface ISaidaView : INotaView

