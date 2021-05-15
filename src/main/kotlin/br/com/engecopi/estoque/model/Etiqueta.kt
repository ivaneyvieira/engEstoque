package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.EtiquetaFinder
import br.com.engecopi.estoque.model.query.QEtiqueta
import br.com.engecopi.framework.model.BaseModel
import javax.persistence.*
import javax.persistence.CascadeType.*
import javax.validation.constraints.Size

@Entity @Table(name = "etiquetas") class Etiqueta : BaseModel() {
  @Size(max = 60) var titulo: String = ""

  @Enumerated(EnumType.STRING) var statusNota: StatusNota? = null

  @Lob var template: String = ""

  @OneToMany(mappedBy = "etiqueta", cascade = [PERSIST, MERGE, REFRESH]) val itensNota: List<ItemNota>? = null
  var etiquetaDefault: Boolean = false
  val isCliente
    get() = titulo.contains("CLiente")

  companion object Find : EtiquetaFinder() {
    fun find(titulo: String?, statusNota: StatusNota?): Etiqueta? {
      titulo ?: return null
      statusNota ?: return null
      return QEtiqueta().titulo.eq(titulo).statusNota.eq(statusNota).findList().firstOrNull()
    }

    fun findByStatus(statusNota: StatusNota?, prefixo: String): List<Etiqueta> {
      return QEtiqueta().statusNota.eq(statusNota).etiquetaDefault.eq(true).titulo.contains(prefixo)
              .orderBy().titulo.asc().findList()
    }
  }
}
