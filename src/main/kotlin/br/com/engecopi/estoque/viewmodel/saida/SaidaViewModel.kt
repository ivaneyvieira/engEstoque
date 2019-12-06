package br.com.engecopi.estoque.viewmodel.saida

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.viewmodel.notaFiscal.INotaView
import br.com.engecopi.estoque.viewmodel.notaFiscal.NotaViewModel
import br.com.engecopi.estoque.viewmodel.notaFiscal.NotaVo
import br.com.engecopi.estoque.viewmodel.notaFiscal.ProdutoVO
import java.time.LocalDate
import java.time.LocalTime

class SaidaViewModel(view: ISaidaView):
  NotaViewModel<SaidaVo, ISaidaView>(view, SAIDA, ENTREGUE, CONFERIDA, abreviacaoDefault) {
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
  
  private val processamento = Processamento(view)
  
  fun processaKey(key: String) = execValue {processamento.processaKey(key)}
  
  fun confirmaProdutos(itens: List<ProdutoVO>, situacao: StatusNota) = execList<ItemNota> {
    itens.firstOrNull()
      ?.value?.nota?.save()
    val listMultable = mutableListOf<ItemNota>()
    itens.forEach {produtoVO ->
      produtoVO.value?.run {
        if(this.id != 0L) refresh()
        
        this.status = situacao
        this.impresso = false
        this.usuario = usuarioDefault
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
        else showWarning("A quantidade do produto ${produto?.codigo} não pode ser maior que $quantidade")
      }
    }
    view.updateView()
    listMultable
  }
  
  fun findByBarcodeProduto(barcode: String?): List<Produto> {
    return if(barcode.isNullOrBlank()) emptyList()
    else Produto.findBarcode(barcode)
  }
}

class SaidaVo: NotaVo(SAIDA, abreviacaoDefault)

interface ISaidaView: INotaView

