package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.Repositories.findByAbreviacao
import br.com.engecopi.estoque.model.Repositories.findByProduto
import br.com.engecopi.estoque.model.finder.ViewProdutoLocFinder
import br.com.engecopi.estoque.model.query.QViewProdutoLoc
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Cache(enableQueryCache = false)
@Entity
@View(name = "t_loc_produtos")
class ViewProdutoLoc(@Id
                     val id: String, val storeno: Int,
                     val codigo: String,
                     val grade: String,
                     val localizacao: String,
                     val abreviacao: String,
                     @ManyToOne(cascade = [])
                     @JoinColumn(name = "produto_id")
                     val produto: Produto,
                     @OneToOne(cascade = [])
                     @JoinColumn(name = "loja_id")
                     val loja: Loja) {
  companion object Find: ViewProdutoLocFinder() {
    fun existsCache(produto: Produto?): Boolean {
      return findByProduto(produto).count() > 0
    }
  
    fun produtosCache(): List<Produto> {
      return Repositories.findByLojaAbreviacao()
        .map {it.produto}
        .distinct()
    }
  
    fun findCache(produto: Produto?): List<ViewProdutoLoc> {
      produto ?: return emptyList()
      return findByProduto(produto)
    }
  
    fun localizacoesAbreviacaoCache(abreviacao: String): List<String> {
      return findByAbreviacao(abreviacao).map {it.localizacao}
        .distinct()
    }
  
    fun localizacoesProdutoCache(produto: Produto?): List<String> {
      produto ?: return emptyList()
      return findByProduto(produto).map {it.localizacao}
        .distinct()
    }
  
    fun localizacoesProduto(produto: Produto?): List<String> {
      val loja = lojaDeposito
      produto ?: return emptyList()
      return QViewProdutoLoc().produto.id.eq(produto.id)
        .findList()
        .asSequence()
        .mapNotNull {it.localizacao}
        .distinct()
        .map {localizacao ->
          val saldo = produto.saldoLoja(loja, localizacao)
          Pair(localizacao, saldo)
        }
        .sortedBy {pair -> -pair.second}
        .map {it.first}
        .toList()
    }
  
    fun abreviacoesProduto(produto: Produto?) =
      QViewProdutoLoc().produto.id.eq(produto?.id).findList().mapNotNull {it.abreviacao}.distinct()
  
    fun filtraLoc(prdno: String?, grade: String?): Boolean {
      val produto = Produto.findProduto(prdno, grade) ?: return false
      val abreviacoes = findCache(produto).map {it.abreviacao}
      return abreviacoes.contains(RegistryUserInfo.abreviacaoDefault)
    }
  
    fun findByCodigoGrade(prdno: String?, grade: String?): List<ViewProdutoLoc> {
      prdno ?: return emptyList()
      grade ?: return emptyList()
      return QViewProdutoLoc().codigo.eq(prdno.padStart(16, ' '))
        .grade.eq(grade)
        .findList()
    }
  }
}
