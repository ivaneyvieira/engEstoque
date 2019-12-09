package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.query.QItemNota

class SaidaViewModel(view: ISaidaView):
  NotaViewModel<SaidaVo, ISaidaView>(view, SAIDA, ENTREGUE, CONFERIDA, abreviacaoDefault) {
  private val processing = SaidaProcessamento()
  
  override fun newBean(): SaidaVo {
    return SaidaVo()
  }
  
  override fun QItemNota.filtroTipoNota(): QItemNota {
    return this
  }
  
  override fun QItemNota.filtroStatus(): QItemNota {
    return status.`in`(ENTREGUE, CONFERIDA)
  }
  
  override fun add(bean: SaidaVo) {
    //Não faze nada
  }
  
  override fun createVo() = SaidaVo()
  
  fun findByBarcodeProduto(barcode: String?): List<Produto> {
    return if(barcode.isNullOrBlank()) emptyList()
    else Produto.findBarcode(barcode)
  }
  
  fun processaKey(key: String) = execValue {
    processing.processaKey(key)
      .apply {
        view.updateView()
      }
  }
  
  fun confirmaProdutos(itens: List<ProdutoVO>, situacao: StatusNota) = execList {
    processing.confirmaProdutos(itens, situacao)
      .apply {
        view.updateView()
      }
  }
}

class SaidaVo: NotaVo(SAIDA, abreviacaoDefault)

interface ISaidaView: INotaView

