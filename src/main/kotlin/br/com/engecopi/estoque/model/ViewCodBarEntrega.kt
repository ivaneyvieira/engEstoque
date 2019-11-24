package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.ViewCodBarEntregaFinder
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_codigo_barra_entrega")
class ViewCodBarEntrega {
  @Id
  @Column(name = "id_itens_nota")
  val id: Long = 0
  val codbar: String = ""
  val storeno: Int = 0
  val numero: String = ""
  val sequencia: Int = 0
  val abreviacao: String = ""
  val codigo: String = ""
  val grade: String = ""
  val quantidade: Int = 0
  
  companion object Find: ViewCodBarEntregaFinder() {
    fun findNota(key: String): ItemNota? {
      val id = where().codbar.eq(key).findList().firstOrNull()?.id ?: return null
      return ItemNota.byId(id) ?: return null
    }
  }
}
