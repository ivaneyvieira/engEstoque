package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.estoque.model.etlSaci.TableName
import br.com.engecopi.utils.localDate
import io.ebean.DB

@TableName("t_pedido_nota_ressuprimento")
class PedidoNotaRessuprimento(id: String,
                              val storenoPedido: Int,
                              val ordno: Int,
                              val dataPedido: Int,
                              val storenoNota: Int,
                              val nfno: Int?,
                              val nfse: String?,
                              val numero: String,
                              val dataNota: Int): EntryID(id) {
  override val chave: String
    get() = "$storenoNota$nfno$nfse"
  
  companion object {
    private val SQL_PEDIDO = """select * from t_pedido_nota_ressuprimento
        |where storenoNota = :lojaTransferencia
        |  AND numero = :numeroSerieTransferencia
      """.trimMargin()
    private val queryPedido =
      DB.findDto(PedidoNotaRessuprimento::class.java, SQL_PEDIDO)
    private val SQL_BAIXA = """select * from t_pedido_nota_ressuprimento
        |where ordno = :numeroPedido
      """.trimMargin()
    private val queryBaixa =
      DB.findDto(PedidoNotaRessuprimento::class.java, SQL_BAIXA)
  
    fun pedidoRessuprimento(lojaTransferencia: Int?, numeroSerieTransferencia: String?): List<NotaBaixaFatura> {
      lojaTransferencia ?: return emptyList()
      numeroSerieTransferencia ?: return emptyList()
    
      return queryPedido
        .setParameter("lojaTransferencia", lojaTransferencia)
        .setParameter("numeroSerieTransferencia", numeroSerieTransferencia)
        .findList()
        .map {nf ->
          NotaBaixaFatura(nf.storenoPedido,
                          nf.ordno.toString(),
                          nf.dataPedido.localDate())
        }
    }
    
    fun notaBaixa(numeroPedido: String?): List<NotaBaixaFatura> {
      numeroPedido ?: return emptyList()
  
      return queryBaixa
        .setParameter("numeroPedido", numeroPedido)
        .findList()
        .map {nf ->
          NotaBaixaFatura(nf.storenoNota,
                          nf.numero,
                          nf.dataNota.localDate())
        }
    }
  }
}