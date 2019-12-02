package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.TipoNota.VENDAF
import br.com.engecopi.estoque.model.finder.ProdutoFinder
import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.saci.saci
import br.com.engecopi.utils.lpad
import io.ebean.annotation.Cache
import io.ebean.annotation.CacheQueryTuning
import io.ebean.annotation.Formula
import io.ebean.annotation.Index
import io.ebean.annotation.Transactional
import java.time.LocalDate
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient
import javax.validation.constraints.Size

@Cache(enableQueryCache = true)
@CacheQueryTuning(maxSecsToLive = 30)
@Entity
@Table(name = "produtos")
@Index(unique = true, columnNames = ["codigo", "grade"])
class Produto: BaseModel() {
  @Size(max = 16)
  var codigo: String = ""
  @Size(max = 8)
  var grade: String = ""
  @Size(max = 16)
  @Index(unique = false)
  var codebar: String = ""
  var dataCadastro: LocalDate = LocalDate.now()
  @OneToMany(mappedBy = "produto", cascade = [REFRESH])
  val itensNota: List<ItemNota>? = null
  @OneToOne(cascade = [])
  //  @FetchPreference(1)
  @JoinColumn(name = "id")
  var vproduto: ViewProduto? = null
  //@FetchPreference(2)
  @OneToMany(mappedBy = "produto", cascade = [REFRESH])
  var viewProdutoLoc: List<ViewProdutoLoc>? = null
  @Formula(select = "LOC.localizacao",
           join = "LEFT join (select produto_id, GROUP_CONCAT(DISTINCT localizacao ORDER BY localizacao SEPARATOR ' -" + " ') as localizacao from t_loc_produtos FORCE INDEX(i2) where storeno = @LOJA_FIELD group by " + "produto_id) AS LOC ON LOC.produto_id = \${ta}.id")
  var localizacao: String? = ""
  @Formula(select = "SAL.saldo_total",
           join = "LEFT JOIN (select produto_id, SUM(quantidade*IF(tipo_mov = 'ENTRADA', 1, -1)*IF(tipo_mov in " + "('INCLUIDA', 'ENTREGUE_LOJA') || tipo_nota IN ('CANCELADA_E', 'CANCELADA_S'), 0, 1)) AS " + "saldo_total from itens_nota AS I inner join notas AS N ON N.id = I.nota_id inner join lojas AS L " + "   ON L.id = N.loja_id WHERE L.numero = @LOJA_FIELD group by produto_id) AS SAL ON SAL.produto_id" + " = \${ta}.id")
  var saldo_total: Int? = 0
  val descricao: String?
    @Transient get() = vproduto?.nome
  val temGrade: Boolean
    get() = grade != ""
  
  fun localizacao(usuario: Usuario?): String? {
    val user = usuario ?: return ""
    val localizacaoUser = user.localizacoesProduto(this)
    val locs = ViewProdutoLoc.findCache(produto = this)
    
    return locs.firstOrNull {localizacaoUser.contains(it.localizacao)}
      ?.localizacao
  }
  
  @Transactional
  fun recalculaSaldos() {
    ViewProdutoLoc.findCache(this)
      .map {it.localizacao}
      .forEach {localizacao ->
        recalculaSaldos(localizacao)
      }
  }
  
  @Transactional
  fun recalculaSaldos(localizacao: String): Int {
    val loja = RegistryUserInfo.lojaDeposito ?: return 0
    var saldo = 0
    val itensNotNull =
      ItemNota.where()
        .produto.id.eq(id)
        .or()
        .nota.loja.equalTo(loja)
        .nota.tipoNota.eq(VENDAF)
        .endOr()
      .localizacao.like(if(localizacao == "") "%" else localizacao)
      .findList()
    itensNotNull
      .sortedWith(compareBy(ItemNota::data, ItemNota::hora))
      .forEach {item ->
        item.refresh()
        saldo += item.quantidadeSaldo
        item.saldo = saldo
        item.update()
      }
    return saldo
  }
  
  companion object Find: ProdutoFinder() {
    fun findProduto(codigo: String?, grade: String?): Produto? {
      codigo ?: return null
      return where().codigo.eq(codigo.trim().lpad(16, " "))
        .grade.eq(grade ?: "")
        .findList()
        .firstOrNull()
    }
    
    fun findBarcode(barcode: String?): List<Produto> {
      val storeno = RegistryUserInfo.lojaDeposito.numero
      barcode ?: return emptyList()
      val listProduto = saci.findBarcode(storeno, barcode)
      return listProduto.mapNotNull {chave -> findProduto(chave.codigo, chave.grade)}
    }
    
    fun findProdutos(codigo: String?): List<Produto> {
      codigo ?: return emptyList()
      
      return where().codigo.eq(codigo.lpad(16, " "))
        .findList()
    }
    
    fun findGradesProduto(codigo: String?): List<String> {
      codigo ?: return emptyList()
      return findProdutos(codigo).map {it.grade}
    }
    
    fun findProdutos(codigo: String?, grades: List<String>): List<Produto> {
      codigo ?: return emptyList()
      return findProdutos(codigo).filter {grades.contains(it.grade)}
    }
    
    fun createProduto(produtoSaci: ViewProdutoSaci?): Produto? {
      produtoSaci ?: return null
      return Produto().apply {
        produtoSaci.let {pSaci ->
          codigo = pSaci.codigo ?: codigo
          grade = pSaci.grade ?: grade
          codebar = pSaci.codebar ?: codebar
        }
      }
    }
    
    fun createProduto(codigoProduto: String?, gradeProduto: String?): Produto? {
      val produtoSaci = ViewProdutoSaci.find(codigoProduto, gradeProduto)
      return createProduto(produtoSaci)
    }
    
    fun findFaixaCodigo(codigoI: String?, codigoF: String?): List<Produto> {
      val prdnoI = codigoI?.lpad(16, " ") ?: return emptyList()
      val prdnoF = codigoF?.lpad(16, " ") ?: return emptyList()
      return where().codigo.between(prdnoI, prdnoF)
        .findList()
        .filtroCD()
    }
    
    fun findFaixaNome(nomeI: String?, nomeF: String?): List<Produto> {
      return where().vproduto.nome.between(nomeI, nomeF)
        .findList()
        .filtroCD()
    }
    
    fun findFaixaFabricante(vendno: Int?): List<Produto> {
      vendno ?: return emptyList()
      return saci.findFornecedor(vendno)
        .mapNotNull {
          findProduto(it.codigo, it.grade)
        }
        .filtroCD()
    }
    
    fun findFaixaCentroLucro(clno: Int?): List<Produto> {
      clno ?: return emptyList()
      return saci.findCentroLucro(clno)
        .mapNotNull {
          findProduto(it.codigo, it.grade)
        }
        .filtroCD()
    }
    
    fun findTipoProduto(typeno: Int?): List<Produto> {
      typeno ?: return emptyList()
      return saci.findTipoProduto(typeno)
        .mapNotNull {
          findProduto(it.codigo, it.grade)
        }
        .filtroCD()
    }
  }
  
  fun saldoLoja(loja: Loja, localizacao: String?): Int {
    localizacao ?: return 0
    if(localizacao == "") return 0
    
    return findItensNota().asSequence()
      .filter {it.nota?.loja?.id == loja.id && it.localizacao == localizacao}
      .sumBy {it.quantidadeSaldo}
  }
  
  fun saldoAbreviacao(loja: Loja, abreviacao: String): Int {
    return ItemNota.where()
      .produto.id.eq(id)
      .nota.loja.eq(loja)
      .localizacao.startsWith(abreviacao)
      .findList()
      .sumBy {it.quantidadeSaldo}
  }
  
  fun saldoTotal(): Int {
    return findItensNota().sumBy {it.quantidadeSaldo}
  }
  
  fun ultimaNota(): ItemNota? {
    return findItensNota().asSequence()
      .sortedBy {it.id}
      .lastOrNull()
  }
  
  fun findItensNota(): List<ItemNota> {
    return ItemNota.where()
      .produto.id.eq(id)
      .findList()
  }
  
  fun localizacoes(abreviacao: String): List<String> {
    return ViewProdutoLoc.localizacoesProduto(produto = this)
      .filter {it.startsWith(abreviacao)}
  }
  
  fun prefixoLocalizacoes(): String {
    val localizacoes = localizacoes(RegistryUserInfo.abreviacaoDefault)
    if(localizacoes.size == 1) return localizacoes[0]
    val localizacoesSplit = localizacoes.map {it.split("[.\\-]".toRegex())}
    val ctParte = localizacoesSplit.asSequence().map {it.size - 1}.min() ?: 0
    for(i in ctParte downTo 0) {
      val prefix = localizacoesSplit.asSequence()
        .map {it.subList(0, i)}
        .map {it.joinToString(separator = ".")}
        .distinct()
        .toList()
  
      if(prefix.count() == 1) return prefix[0]
    }
    return ""
  }
  
  val barcodeGtin
    get(): List<String> {
      val storeno = lojaDeposito.numero
      val chave = saci.findBarcode(storeno, codigo, grade)
      return chave.map {it.barcode}
    }
}

private fun List<Produto>.filtroCD(): List<Produto> {
  return this.filter {
    it.localizacoes(RegistryUserInfo.abreviacaoDefault)
      .any {loc -> loc.startsWith(RegistryUserInfo.abreviacaoDefault)}
  }
}

data class LocProduto(val localizacao: String): Comparable<LocProduto> {
  val prefixo = localizacao.split("-").getOrNull(0) ?: localizacao
  val abreviacao = localizacao.split('.').getOrNull(0) ?: ""
  
  override fun compareTo(other: LocProduto): Int {
    return localizacao.compareTo(other.localizacao)
  }
  
  override fun toString(): String {
    return localizacao
  }
}
