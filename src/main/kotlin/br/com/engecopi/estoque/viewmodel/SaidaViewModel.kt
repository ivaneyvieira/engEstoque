package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LancamentoOrigem.DEPOSITO
import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaItens
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.ViewCodBarConferencia
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.utils.mid
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
  
  fun processaKey(key: String) = execValue {
    val notaItens = processaKeyNumeroNota(key)
    val ret = if(notaItens.vazio) processaKeyBarcodeConferencia(key)
    else notaItens
    view.updateView()
    ret
  }
  
  private fun processaKeyNumero(loja: Loja, numeroNota: String): NotaItens {
    val notasSaci =
      Nota.findNotaSaidaSaci(loja, numeroNota)
        .filter {loc ->
          loc.localizacaoes()
            .any {it.abreviacao == abreviacaoDefault}
        }
    val notaSaci = notasSaci.firstOrNull() ?: return NotaItens.VAZIO
    return if(usuarioDefault.isTipoCompativel(notaSaci.tipoNota())) Nota.createNotaItens(notasSaci).apply {
      this.nota?.lancamentoOrigem = DEPOSITO
    }
    else NotaItens.VAZIO
  }
  
  private fun processaKeyBarcodeConferencia(key: String): NotaItens {
    val item = ViewCodBarConferencia.findNota(key) ?: return NotaItens.VAZIO
    if(item.abreviacao != abreviacaoDefault) throw EViewModel("Esta nota não pertence ao cd $abreviacaoDefault")
    val nota = Nota.findSaida(item.storeno, item.numero) ?: return NotaItens.VAZIO
    if(nota.lancamentoOrigem != EXPEDICAO) throw EViewModel("Essa nota não foi lançada pela a expedição")
    return NotaItens(nota, nota.itensNota())
  }
  
  private fun processaKeyNumeroNota(key: String): NotaItens {
    val storeno = if(key.isNotEmpty()) key.mid(0, 1).toIntOrNull() ?: return NotaItens.VAZIO
    else return NotaItens.VAZIO
    val loja = Loja.findLoja(storeno) ?: return NotaItens.VAZIO
    val numero = if(key.length > 1) key.mid(1) else return NotaItens.VAZIO
    val notaItem = processaKeyNumero(loja, numero)
    return if(notaItem.nota?.tipoNota == VENDAF || loja.numero == lojaDeposito.numero) {
      notaItem
    }
    else NotaItens.VAZIO
  }
  
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
  
  fun processaBarcodeProduto(barcode: String?): List<Produto> {
    return if(barcode.isNullOrBlank()) emptyList()
    else Produto.findBarcode(barcode)
  }
}

class SaidaVo: NotaVo(SAIDA, abreviacaoDefault)

interface ISaidaView: INotaView
