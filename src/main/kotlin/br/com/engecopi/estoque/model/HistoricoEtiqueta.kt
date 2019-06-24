package br.com.engecopi.estoque.model


import br.com.engecopi.estoque.model.finder.HistoricoEtiquetaFinder
import br.com.engecopi.framework.model.BaseModel
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "hitorico_etiquetas")
class HistoricoEtiqueta(
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var usuario: Usuario,
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var produto: Produto,
  var data: LocalDate,
  var hora: LocalTime,
  @Lob
  var print: String) : BaseModel() {

  companion object Find : HistoricoEtiquetaFinder()
}
