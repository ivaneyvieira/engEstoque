package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.EntryID
import br.com.engecopi.utils.localDate
import io.ebean.DB

class PedidoNotaRessuprimento(
  id: String,
  val storenoPedido: Int,
  val ordno: Int,
  val dataPedido: Int,
  val storenoNota: Int,
  val nfno: Int,
  val nfse: String,
  val numero: String,
  val dataNota: Int
) : EntryID(id) {
  override val chave: String
    get() = "$storenoNota$nfno$nfse"

  companion object {
    fun pedidoRessuprimento(lojaTransferencia: Int?, numeroSerieTransferencia: String?): List<NotaBaixaFatura> {
      lojaTransferencia ?: return emptyList()
      numeroSerieTransferencia ?: return emptyList()
      val sql = """select * from t_pedido_nota_ressuprimento
        |where storenoNota = :lojaTransferencia
        |  AND numero = :numeroSerieTransferencia
      """.trimMargin()
      return DB.findDto(PedidoNotaRessuprimento::class.java, sql).setParameter("lojaTransferencia", lojaTransferencia)
        .setParameter("numeroSerieTransferencia", numeroSerieTransferencia).findList().map { nf ->
          NotaBaixaFatura(
            nf.storenoPedido, nf.ordno.toString(), nf.dataPedido.localDate()
          )
        }
    }

    fun notaBaixa(numeroPedido: String?): List<NotaBaixaFatura> {
      numeroPedido ?: return emptyList()
      val sql = """select * from t_pedido_nota_ressuprimento
        |where ordno = :numeroPedido
      """.trimMargin()
      return DB.findDto(PedidoNotaRessuprimento::class.java, sql).setParameter("numeroPedido", numeroPedido).findList()
        .map { nf ->
          NotaBaixaFatura(
            nf.storenoNota, nf.numero, nf.dataNota.localDate()
          )
        }
    }
  }
}