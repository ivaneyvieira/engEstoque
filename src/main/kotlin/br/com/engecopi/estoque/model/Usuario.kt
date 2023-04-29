package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.UsuarioFinder
import br.com.engecopi.estoque.model.query.QUsuario
import br.com.engecopi.estoque.model.query.QViewProdutoLoc
import br.com.engecopi.framework.model.AppPrinter
import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.saci.saci
import io.ebean.annotation.Index
import io.ebean.annotation.Length
import javax.persistence.CascadeType.*
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Size

@Entity
@Table(name = "usuarios")
class Usuario : BaseModel() {
  @Size(max = 8)
  @Index(unique = true)
  var loginName: String = ""

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var loja: Loja? = null

  @Length(4000)
  var localizacaoes: String = ""

  @Length(4000)
  var notaSeries: String = ""

  @Length(40)
  var impressora: String = ""

  @OneToMany(mappedBy = "usuario", cascade = [PERSIST, MERGE, REFRESH])
  val itensNota: List<ItemNota>? = null
  var locais: List<String>
    get() = localizacaoes.split(",").asSequence().filter { it.isNotBlank() }.map { it.trim() }.toList()
    set(value) {
      localizacaoes = value.asSequence().sorted().joinToString()
    }
  var series: List<NotaSerie>
    get() = notaSeries.split(",").filter { it.isNotBlank() }.mapNotNull { mapNotaSerie(it) }.toList()
    set(value) {
      notaSeries = value.map { it.id.toString() }.sorted().joinToString()
    }
  val isEstoqueExpedicao
    get() = !admin && expedicao && estoque
  val isEstoqueVendaFutura
    get() = !admin && entregaFutura
  val isEstoqueRetiraFutura
    get() = !admin && retiraFutura

  private fun mapNotaSerie(idStr: String): NotaSerie? {
    val id = idStr.trim().toLongOrNull() ?: return null
    return NotaSerie.values.find { it.id == id }
  }

  private fun usuarioSaci() = saci.findUser(loginName)

  var admin: Boolean = false
  var estoque: Boolean = true
  var expedicao: Boolean = false
  var painel: Boolean = false
  var configuracao: Boolean = false
  var entregaFutura: Boolean = false
  var retiraFutura: Boolean = false
  var etiqueta: Boolean = true
  var ressuprimento: Boolean = false
  var abastecimento: Boolean = false
  val nome: String?
    get() = usuarioSaci()?.name

  fun temProduto(produto: Produto?): Boolean {
    produto ?: return false
    return ViewProdutoLoc.existsCache(produto)
  }

  fun impressoraExpedicao(): String {
    val impressoraDefault = if (loginName in listOf("CD5A", "EXP4B")) "CD5A" else "EXP4"
    val saciUser = usuarioSaci() ?: return impressoraDefault
    val impressoraUsuario = saciUser.impressora ?: return impressoraDefault

    val impressoras = AppPrinter.appPrinterNames
    return if (impressoraUsuario in impressoras) impressoraUsuario else impressoraDefault
  }

  fun impressoraSaci(): String {
    val saciUser = usuarioSaci() ?: return impressora
    val impressoraUsuario = saciUser.impressora ?: return impressora

    val impressoras = AppPrinter.appPrinterNames
    return if (impressoraUsuario in impressoras) impressoraUsuario else impressora
  }

  fun localizacoesProduto(produto: Produto): List<String> {
    return QViewProdutoLoc().produto.equalTo(produto).or().loja.equalTo(loja).loja.equalTo(null).endOr()
      .or().abreviacao.isIn(locais).localizacao.isIn(locais).endOr().findList().mapNotNull { it.localizacao }
  }

  fun isTipoCompativel(tipo: TipoNota?): Boolean {
    tipo ?: return false
    return series.any { it.tipoNota == tipo } || admin
  }

  val produtoLoc: List<Produto>
    get() {
      return locais.flatMap { loc ->
        QViewProdutoLoc().loja.eq(loja).or().abreviacao.eq(loc).localizacao.eq(loc).endOr().findList()
          .map { it.produto }
      }
    }

  companion object Find : UsuarioFinder() {
    fun findUsuario(loginName: String?): Usuario? {
      if (loginName.isNullOrBlank()) return null
      return QUsuario().loginName.eq(loginName).findList().firstOrNull()
    }

    fun nomeSaci(value: String): String {
      return saci.findUser(value)?.name ?: ""
    }

    fun abreviacaoes(username: String?): List<String> {
      return findUsuario(loginName = username)?.let { usuario ->
        if (usuario.estoque) {
          val locais = usuario.locais
          if (locais.isEmpty()) usuario.loja?.findAbreviacores()
          else locais
        } else emptyList()
      } ?: emptyList()
    }

    fun findLoginUser() = saci.findLoginUser()
  }
}

