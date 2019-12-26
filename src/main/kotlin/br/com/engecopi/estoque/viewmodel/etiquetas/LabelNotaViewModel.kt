package br.com.engecopi.estoque.viewmodel.etiquetas

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_E
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_S
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta.ENTREGA
import br.com.engecopi.estoque.viewmodel.etiquetas.ETipoEtiqueta.LANCAMENTO
import br.com.engecopi.framework.viewmodel.EViewModelError
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import java.time.LocalDate

class LabelNotaViewModel(view: ILabelNotaView): ViewModel<ILabelNotaView>(view) {
  fun impressaoNota(): String? = exec {
    val etiqueta = etiqueta() ?: throw EViewModelError("Não foi encontrado nenhuma etiqueta")
    val itensNota = view.listaNota.flatMap {nota ->
      nota.itensNota()
    }
    imprimir(itensNota, etiqueta)
  }
  
  private fun etiqueta(): Etiqueta? {
    val tipoEtiqueta = view.tipoEtiqueta ?: throw EViewModelError("Tipo de etiqueta não informado")
    return when(tipoEtiqueta) {
      LANCAMENTO -> Etiqueta.findByStatus(INCLUIDA, "")
      ENTREGA    -> Etiqueta.findByStatus(CONFERIDA, "ETENT")
    }.firstOrNull()
  }
  
  private fun imprimir(itens: List<ItemNota>, etiqueta: Etiqueta): String {
    return itens.map {imprimir(it, etiqueta)}
      .distinct()
      .joinToString(separator = "\n")
  }
  
  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta): String {
    itemNota ?: return ""
    val print = itemNota.printEtiqueta()
    if(!RegistryUserInfo.usuarioDefault.admin) itemNota.let {item ->
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
    view.listaNota = listOf(NotaLabelVo(nota))
  }
}

interface ILabelNotaView: IView {
  var listaNota: List<NotaLabelVo>
  var tipoEtiqueta: ETipoEtiqueta?
  var numeroNota: String?
}

enum class ETipoEtiqueta(val descricao: String) {
  LANCAMENTO("Lançamento"),
  ENTREGA("Entrega")
}

class NotaLabelVo(private val nota: Nota) {
  fun itensNota(): List<ItemNota> {
    return nota.itensNota()
  }
  
  val numero
    get() = "${nota.loja?.numero}${nota.numero}"
  val dataEmissao: LocalDate?
    get() = nota.dataEmissao
  val numeroBaixa
    get() = nota.numeroBaixa()
  val dataBaixa: LocalDate?
    get() = nota.dataBaixa()
  val lancamento: LocalDate?
    get() = nota.data
  val localizacao
    get() = nota.itensNota().mapNotNull {it.abreviacao}.distinct().joinToString(separator = "/")
  val usuario
    get() = nota.usuario?.nome
  val rotaDescricao
    get() = if(nota.tipoNota == TRANSFERENCIA_E || nota.tipoNota == TRANSFERENCIA_S) nota.rota
    else ""
  val cliente
    get() = nota.cliente
}