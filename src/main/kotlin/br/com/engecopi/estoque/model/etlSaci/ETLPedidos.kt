package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.Abreviacao
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.dtos.PedidoSaci
import br.com.engecopi.saci.saci
import br.com.engecopi.utils.CupsUtils
import br.com.engecopi.utils.ECupsPrinter
import br.com.engecopi.utils.format
import br.com.engecopi.utils.localDate
import io.ebean.DB

class ETLPedidos(): ETL<PedidoSaci>() {
  override val sqlDelete = "DELETE FROM t_pedido where id = :id"
  override val sqlInsert = """
      INSERT INTO t_pedido(id, rota, storeno, numero, date, clienteName, abreviacao, nfno, nfse, status)
      VALUES(:id, :rota, :storeno, :numero, :date, :clienteName, :abreviacao, :nfno, :nfse, :status)
    """.trimIndent()
  override val sqlUpdate = """
      UPDATE t_pedido 
      SET  abreviacao=:abreviacao, 
           status=:status
      WHERE id = :id
    """.trimIndent()

  companion object: ETLThread<PedidoSaci>(ETLPedidos()) {
    val sql
      get() = """select id, rota, storeno, numero, date, clienteName, abreviacao, nfno, nfse, status 
      |from t_pedido
      |""".trimMargin()

    override fun getSource() = saci.findPedidoTransferencia()

    override fun getTarget() = DB
      .findDto(PedidoSaci::class.java, sql)
      .findList()

    init {
      addListenerInsert("ImprimeInsert") {pedido ->
        if(pedido.status == 2) {
          try {
            val etiqueta = etiquetaPedido(pedido)
            val impressora = Abreviacao.findByAbreviacao(pedido.abreviacao)?.impressora ?: ""
            //CupsUtils.printCups(impressora, etiqueta)
          } catch(e: ECupsPrinter) {
            //Vazio
          }
        }
      }
    }
  }
}

fun etiquetaPedido(pedido: PedidoSaci): String {
  val data = pedido.date?.localDate()
    .format()
  return """^XA
  ^FT50,060^A0N,40,40^FH^FDPedido de transferencia^FS
  ^FT50,130^A0N,40,40^FH^FDNumero: ${pedido.numero}^FS
  ^FT50,180^A0N,40,40^FH^FDLocalizacao: ${pedido.abreviacao}^FS
  ^FT50,230^A0N,40,40^FH^FDRota: ${pedido.rota}^FS
  ^FT50,280^A0N,40,40^FH^FDData: ${data}^FS
  ^XZ"""
}
