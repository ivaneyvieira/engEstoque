package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.ViewPedidoNotaRessuprimentoBaixaFinder
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_pedido_nota_ressuprimento")
class ViewPedidoNotaRessuprimentoBaixa(
  @Id
  @Column(name = "id")
  val id: String,
  val storenoPedido: Int,
  val ordno: Int,
  val dataPedido: Int,
  val storenoNota: Int,
  val nfno: Int,
  val nfse: String,
  val numero: String,
  val dataNota: Int,
  @ManyToOne
  var loja: Loja,
  @ManyToOne
  var nota: Nota
                                      ) {
  companion object Find: ViewPedidoNotaRessuprimentoBaixaFinder()
}
