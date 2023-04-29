package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.ValidadeFinder
import br.com.engecopi.estoque.model.query.QValidade
import br.com.engecopi.framework.model.BaseModel
import io.ebean.annotation.Index
import io.ebean.annotation.Indices
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "validades")
@Indices(Index(unique = true, columnNames = ["meses_validade", "meses_fabricacao"]))
class Validade : BaseModel() {
  var mesesValidade: Int = 0
  var mesesFabricacao: Int = 0

  companion object Find : ValidadeFinder() {
    fun find(mesesValidade: Int?): Validade? {
      return QValidade().mesesValidade.eq(mesesValidade).findList().firstOrNull()
    }
  }
}
