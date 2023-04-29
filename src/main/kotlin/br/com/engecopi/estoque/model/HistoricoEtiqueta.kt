package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.HistoricoEtiquetaFinder
import br.com.engecopi.framework.model.BaseModel
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.CascadeType.*
import javax.persistence.Entity
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "hitorico_etiquetas")
class HistoricoEtiqueta(
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH]) var usuario: Usuario,
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH]) var produto: Produto,
  var data: LocalDate = LocalDate.now(),
  var hora: LocalTime = LocalTime.now(),
  @Lob var print: String,
  var gtin: String,
  var gtinOk: Boolean = true
) : BaseModel() {
  companion object Find : HistoricoEtiquetaFinder() {
    fun save(produto: Produto, gtin: String, print: String) {
      val hist = HistoricoEtiqueta(
        usuario = RegistryUserInfo.usuarioDefault, produto = produto, print = print, gtin = gtin
      )
      hist.save()
    }
  }
}
