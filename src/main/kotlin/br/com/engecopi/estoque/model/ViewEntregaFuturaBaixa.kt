package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.ViewEntregaFuturaBaixaFinder
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_entrega_futura")
class ViewEntregaFuturaBaixa(
  @Id
  @Column(name = "id")
  val id: String,
  val storenoVenda: Int,
  val numeroVenda: String,
  val nfnoVenda: Int,
  val nfseVenda: String,
  val dataVenda: Int,
  val storenoEntrega: Int,
  val numeroEntrega: String,
  val nfnoEntrega: Int,
  val nfseEntrega: String,
  val dataEntrega: Int,
  val nfekeyEntrega: String?,
  @ManyToOne
  var loja: Loja,
  @ManyToOne
  var nota: Nota
                            ) {
  companion object Find: ViewEntregaFuturaBaixaFinder()
}
