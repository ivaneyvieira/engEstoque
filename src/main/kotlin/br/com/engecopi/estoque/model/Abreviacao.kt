package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.model.finder.AbreviacaoFinder
import br.com.engecopi.estoque.model.query.QAbreviacao
import br.com.engecopi.framework.model.BaseModel
import io.ebean.annotation.Index
import io.ebean.annotation.Length
import javax.persistence.CascadeType.*
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
  var loja: Loja,
  var expedicao: Boolean,
  @Length(15)
  var impressora: String?) : BaseModel() {
  val printer
    get() = Printer(impressora ?: "")

  companion object Find : AbreviacaoFinder() {
    fun findByAbreviacao(abreviacao: String?): Abreviacao? {
      abreviacao ?: return null
      val loja = lojaDeposito
      return QAbreviacao().abreviacao.eq(abreviacao).loja.equalTo(loja).findList().firstOrNull()
    }

    private fun addAbreviacao(abreviacao: String) {
      if (findByAbreviacao(abreviacao) == null) {
        Abreviacao(abreviacao, lojaDeposito, false, "").save()
      }
    }

    private fun updateAbreviacao(loja: Loja) {
      val abreviacaoes = Repositories.findByLoja(loja).map { it.abreviacao }.distinct().sorted()
      abreviacaoes.forEach { addAbreviacao(it) }
    }

    fun findAll(): List<Abreviacao> = all().sortedBy { it.abreviacao }
  }
}
