package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.EtiquetaFinder
import br.com.engecopi.framework.model.BaseModel
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Size

@Entity
@Table(name = "etiquetas")
class Etiqueta: BaseModel() {
  @Size(max = 60)
  var titulo: String = ""
  @Enumerated(EnumType.STRING)
  var statusNota: StatusNota? = null
  @Lob
  var template: String = ""
  @OneToMany(mappedBy = "etiqueta", cascade = [PERSIST, MERGE, REFRESH])
  val itensNota: List<ItemNota>? = null
  var etiquetaDefault: Boolean = false
  val isCliente
    get() = titulo.contains("CLiente")

  companion object Find: EtiquetaFinder() {
    fun find(titulo: String?, statusNota: StatusNota?): Etiqueta? {
      titulo ?: return null
      statusNota ?: return null
      return where().titulo.eq(titulo)
        .statusNota.eq(statusNota)
        .findList()
        .firstOrNull()
    }

    fun findByStatus(statusNota: StatusNota?): List<Etiqueta> =
      where().statusNota.eq(statusNota).etiquetaDefault.eq(true).orderBy().titulo.asc().findList()
  }

  fun updateOutros() {
    //db().update(Etiqueta::class.java)
    //   .set("etiqueta_default", false)
    //   .where()
    //   .eq("status_nota", statusNota)
    //   .ne("id", id)
    //  .update()
  }

  fun imprimivel(tipoNota: TipoNota): Boolean {
    // return tipoNota != VENDA || isCliente
    return true
  }
}
