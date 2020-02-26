package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.ViewTransferenciaAutomaticaBaixaFinder
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_transferencia_automatica")
class ViewTransferenciaAutomaticaBaixa(
  @Id
  @Column(name = "id")
  val id: String,
  val storeno: Int,
  val pdvno: Int,
  val xano: Int,
  val data: Int,
  val storenoFat: Int,
  val nffat: String,
  val storenoTransf: Int,
  val nftransf: String,
  @ManyToOne
  var loja: Loja,
  @ManyToOne
  var nota: Nota) {
  companion object Find: ViewTransferenciaAutomaticaBaixaFinder()
}
