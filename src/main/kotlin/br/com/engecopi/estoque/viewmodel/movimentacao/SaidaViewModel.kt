package br.com.engecopi.estoque.viewmodel.movimentacao

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.ProdutoValidade
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.fisrtProdutoValidade
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.utils.localDate
import java.time.LocalDate

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

  fun findByBarcodeProduto(barcode: String?): BarcodeVolume? {
    val splitBarcode = barcode?.split(" ").orEmpty()
    val barcodeProduto = splitBarcode.getOrNull(0) ?: return null
    val produto = find.findByBarcodeProduto(barcodeProduto).firstOrNull() ?: return null
    val numValidade = barcode?.split(" ")?.getOrNull(1)
    val dataValidade = numValidade?.let { numVal ->
      val dataSaci = "${numVal}00".toIntOrNull()
      dataSaci?.localDate()
    }
    val quantidadeVolume = barcode?.split(" ")?.getOrNull(2)?.toIntOrNull()
    val volume = barcode?.split(" ")?.getOrNull(3)?.toIntOrNull()

    val produtoValidade = fisrtProdutoValidade(produto.codigo)
    return BarcodeVolume(produto, dataValidade, quantidadeVolume, volume, produtoValidade)
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

data class BarcodeVolume(
  val produto: Produto,
  val dataValidade: LocalDate?,
  val quantidadeVolume: Int?,
  val volume: Int?,
  val produtoValidade: ProdutoValidade?,
                        ) {
  fun isDataVencimentoValida(): Boolean {
    val validadeAtual = produtoValidade?.dataValidade ?: return true
    val dataEtiqueta = dataValidade?.withDayOfMonth(1) ?: return true
    return validadeAtual.isAfter(dataEtiqueta) || dataEtiqueta == dataValidade
  }
}

interface ISaidaView : INotaView

