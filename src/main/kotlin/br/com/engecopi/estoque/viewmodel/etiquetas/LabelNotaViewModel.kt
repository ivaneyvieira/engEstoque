package br.com.engecopi.estoque.viewmodel.etiquetas

import br.com.engecopi.estoque.model.*
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_E
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_S
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta.ENTREGA
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta.LANCAMENTO
import br.com.engecopi.estoque.viewmodel.expedicao.PacoteImpressao
import br.com.engecopi.framework.model.PrinterInfo
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import java.time.LocalDate

class LabelNotaViewModel(view: ILabelNotaView) : ViewModel<ILabelNotaView>(view) {
  fun impressaoNota(): List<PacoteImpressao> = execList {
    val etiqueta = etiqueta() ?: throw EViewModelError("Não foi encontrado nenhuma etiqueta")
    val itensNota = view.listaNota.map { nota ->
      nota.itensNota
    }
    val tipoEtiqueta = view.tipoEtiqueta ?: throw EViewModelError("Tipo de etiqueta não informado")
    when (tipoEtiqueta) {
      LANCAMENTO -> imprimirLancamento(itensNota, etiqueta)
      ENTREGA    -> imprimirEntrega(itensNota, etiqueta)
    }
  }

  private fun imprimirLancamento(itensNota: List<ItemNota>, etiqueta: Etiqueta): List<PacoteImpressao> {
    val agrupaAbreviacao = itensNota.groupBy { it.abreviacao }
    return agrupaAbreviacao.mapNotNull { (abrev, itens) ->
      val impressora = abrev?.impressora ?: return@mapNotNull null
      val text = imprimir(itens, etiqueta)
      PacoteImpressao(Printer(impressora), text)
    }
  }

  private fun imprimirEntrega(itensNota: List<ItemNota>, etiqueta: Etiqueta): List<PacoteImpressao> {
    val groupOrigem = itensNota.groupBy { it.nota?.lancamentoOrigem }

    return groupOrigem.mapNotNull { (origem, itens) ->
      val impressora = origem?.printer() ?: return@mapNotNull null
      val text = imprimir(itens, etiqueta)
      PacoteImpressao(impressora, text)
    }
  }

  private fun etiqueta(): Etiqueta? {
    val tipoEtiqueta = view.tipoEtiqueta ?: throw EViewModelError("Tipo de etiqueta não informado")
    return when (tipoEtiqueta) {
      LANCAMENTO -> Etiqueta.findByStatus(INCLUIDA, "")
      ENTREGA    -> Etiqueta.findByStatus(CONFERIDA, "ETENT")
    }.firstOrNull()
  }

  private fun imprimir(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.map { imprimir(it, etiqueta) }.distinct().joinToString(separator = "\n")
  }

  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    if (!RegistryUserInfo.usuarioDefault.admin) itemNota.let { item ->
      item.refresh()
      item.impresso = true
      item.update()
    }

    return print.print(etiqueta.template)
  }

  fun processaFiltro() = exec {
    val numeroKey = view.numeroNota ?: ""
    val key = KeyNota(numeroKey)
    val nota = Nota.findSaida(key.storeno, key.numero) ?: throw EViewModelError("Nota não encontrada")
    view.listaNota = nota.itensNota().groupBy { it.abreviacao }.values.mapNotNull { it.firstOrNull() }
            .map { NotaLabelVo(it) }
  }
}

interface ILabelNotaView : IView {
  var listaNota: List<NotaLabelVo>
  var tipoEtiqueta: ETipoEtiqueta?
  var numeroNota: String?
  var impressora: PrinterInfo?
}

enum class ETipoEtiqueta(val descricao: String) {
  LANCAMENTO("Lançamento"),
  ENTREGA("Entrega")
}

class NotaLabelVo(val itensNota: ItemNota) {
  val nota = itensNota.nota
  val numero
    get() = "${nota?.loja?.numero}${nota?.numero}"
  val dataEmissao: LocalDate?
    get() = nota?.dataEmissao
  val numeroBaixa
    get() = nota?.notaBaixa() ?: emptyList()
  val dataBaixa: LocalDate?
    get() = nota?.dataBaixa()
  val lancamento: LocalDate?
    get() = nota?.data
  val localizacao
    get() = itensNota.abreviacao?.abreviacao
  val usuario
    get() = nota?.usuario?.nome
  val rotaDescricao
    get() = if (nota?.tipoNota == TRANSFERENCIA_E || nota?.tipoNota == TRANSFERENCIA_S) nota.rota
    else ""
  val cliente
    get() = nota?.cliente
}