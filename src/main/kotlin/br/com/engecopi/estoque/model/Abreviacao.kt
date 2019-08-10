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
class Abreviacao(
  @Index(unique = true)
  @Length(6)
  var abreviacao: String,
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var loja: Loja,
  var expedicao: Boolean,
  @Length(15)
  var impressora: String
                ): BaseModel() {
  companion object Find: AbreviacaoFinder() {
    fun findByAbreviacao(abreviacao: String): Boolean {
      return where().abreviacao.eq(abreviacao)
        .loja.equalTo(lojaDefault)
        .exists()
    }

    fun addAbreviacao(abreviacao: String) {
      if(!findByAbreviacao(abreviacao)) {
        Abreviacao(abreviacao, lojaDefault, false, "").save()
      }
    }

    fun updateAbreviacao() {
      val abreviacaoes = Repositories.findByLoja(lojaDefault)
        .map {it.abreviacao}
        .distinct()
        .sorted()
      abreviacaoes.forEach {addAbreviacao(it)}
    }

    fun findAll(): List<Abreviacao> = all().sortedBy {it.abreviacao}
  }
}
