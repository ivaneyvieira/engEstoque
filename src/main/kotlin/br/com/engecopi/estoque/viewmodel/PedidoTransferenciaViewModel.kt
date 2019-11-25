package br.com.engecopi.estoque.viewmodel

import br.com.astrosoft.utils.localDate
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.dtos.PedidoSaci
import br.com.engecopi.estoque.model.etlSaci.ETLPedidos
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel

class PedidoTransferenciaViewModel(view: IPedidoTransferenciaView): ViewModel<IPedidoTransferenciaView>(view) {
  val pedidosTransferencia = mutableListOf<PedidoTransferenciaVo>()
  
  init {
    refresh()
  }
  
  fun refresh() = exec {
    pedidosTransferencia.clear()
    //val storeno = RegistryUserInfo.lojaDefault.numero
    val notas = ETLPedidos.listDados.filter {pedido -> RegistryUserInfo.abreviacaoDefault == pedido.abreviacao}
      .map {PedidoTransferenciaVo(it)}
    pedidosTransferencia.addAll(notas)
    view.updateView()
  }
}

class PedidoTransferenciaVo(val pedidoSaci: PedidoSaci) {
  val numero = "${pedidoSaci.storeno} ${pedidoSaci.numero}"
  val lojaNF = Loja.findLoja(pedidoSaci.storeno)
  val lancamento = pedidoSaci.date?.localDate()
  val abreviacao = pedidoSaci.abreviacao
  val rotaDescricao = pedidoSaci.rota
  val cliente = pedidoSaci.clienteName
  val nfTransferencia: String
    get() {
      pedidoSaci.nfno ?: return ""
      return if(pedidoSaci.nfse.isNullOrBlank()) pedidoSaci.nfno
      else "${pedidoSaci.nfno}/${pedidoSaci.nfse}"
    }
  val status = pedidoSaci.status
}

interface IPedidoTransferenciaView: IView {
  fun updateView()
}