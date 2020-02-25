package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.PedidoSaci
import br.com.engecopi.saci.saci
import br.com.engecopi.utils.format
import br.com.engecopi.utils.localDate

class ETLPedidos: ETL<PedidoSaci>() {
 companion object: ETLThread<PedidoSaci>(ETLPedidos(), 60) {
   override fun getSource() = saci.findPedidoTransferencia()
 }
}

fun etiquetaPedido(pedido: PedidoSaci): String {
  val data = pedido.date?.localDate().format()
  return """^XA
  ^FT50,060^A0N,40,40^FH^FDPedido de transferencia^FS
  ^FT50,130^A0N,40,40^FH^FDNumero: ${pedido.numero}^FS
  ^FT50,180^A0N,40,40^FH^FDLocalizacao: ${pedido.abreviacao}^FS
  ^FT50,230^A0N,40,40^FH^FDRota: ${pedido.rota}^FS
  ^FT50,280^A0N,40,40^FH^FDData: ${data}^FS
  ^XZ"""
}
