package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.finder.AbreviacaoFinder
import br.com.engecopi.framework.model.BaseModel
import io.ebean.annotation.Index
import io.ebean.annotation.Length
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "abreviacoes")
@Index(columnNames = ["loja_id", "abreviacao"], unique = true)
class Abreviacao(
  @Length(6)
  var abreviacao: String,
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var loja: Loja, var expedicao: Boolean,
  @Length(15)
  var impressora: String): BaseModel() {
  companion object Find: AbreviacaoFinder() {
    fun findByAbreviacao(abreviacao: String?): Abreviacao? {
      abreviacao ?: return null
      return where().abreviacao.eq(abreviacao).loja.equalTo(lojaDefault).findList().firstOrNull()
    }

    fun addAbreviacao(abreviacao: String) {
      val loja = lojaDefault ?: return
      if(findByAbreviacao(abreviacao) == null) {
        Abreviacao(abreviacao, loja, false, "").save()
      }
    }

    fun updateAbreviacao() {
      val abreviacaoes = Repositories.findByLoja(lojaDefault).map {it.abreviacao}.distinct().sorted()
      abreviacaoes.forEach {addAbreviacao(it)}
    }

    fun findAll(): List<Abreviacao> = all().sortedBy {it.abreviacao}
  }
}
