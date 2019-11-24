package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.framework.model.DB
import io.ebean.Ebean
import java.time.LocalDateTime

object Repositories {
  private var time: LocalDateTime = LocalDateTime.now()
    .minusSeconds(100)
  private lateinit var viewProdutosLocProdutoKey: Map<ProdutoKey, List<ViewProdutoLoc>>
  private lateinit var viewProdutosLocLojaAbreviacaoKey: Map<LojaAbreviacaoKey, List<ViewProdutoLoc>>
  private lateinit var viewProdutosLocLojaKey: Map<Int, List<ViewProdutoLoc>>
  private lateinit var viewProdutosLocAbreviacaoKey: Map<String, List<ViewProdutoLoc>>
  
  init {
    newViewProdutosLoc()
  }
  
  fun updateViewProdutosLoc() {
    newViewProdutosLoc()
  }
  
  @Synchronized
  private fun atualizaRepositorio() {
    updateTabelas()
    val serverCacheManager = Ebean.getServerCacheManager()
    serverCacheManager.clear(ViewProdutoLoc::class.java)
    val list = ViewProdutoLoc.where()
      .fetch("produto")
      .fetch("loja")
      .findList()
      .toList()
    viewProdutosLocProdutoKey = list.groupBy {ProdutoKey(it.produto.id, it.storeno, it.abreviacao)}
    viewProdutosLocLojaAbreviacaoKey = list.groupBy {LojaAbreviacaoKey(it.storeno, it.abreviacao)}
    viewProdutosLocLojaKey = list.groupBy {it.storeno}
    viewProdutosLocAbreviacaoKey = list.groupBy {it.abreviacao}
  }
  
  private fun newViewProdutosLoc() {
    val agora = LocalDateTime.now()
      .minusSeconds(10)
    if(agora >= time) {
      time = LocalDateTime.now()
      atualizaRepositorio()
    }
  }
  
  private fun updateTabelas() {
    val sql = """update t_loc_produtos AS T
  left join produtos AS P
     USING(codigo, grade)
SET T.produto_id = IFNULL(P.id, 0)
WHERE T.produto_id <> P.id;

update tab_produtos AS T
  left join produtos AS P
     USING(codigo, grade)
SET T.produto_id = IFNULL(P.id, 0)
WHERE T.produto_id <> P.id;
"""
    DB.sciptSql(sql)
  }
  
  fun findByProduto(produto: Produto?): List<ViewProdutoLoc> {
    produto ?: return emptyList()
    return viewProdutosLocProdutoKey[ProdutoKey(produtoId = produto.id,
                                                storeno = lojaDefault.numero,
                                                abreviacao = abreviacaoDefault)].orEmpty()
  }
  
  fun findByLoja(loja: Loja?): List<ViewProdutoLoc> {
    loja ?: return emptyList()
    return viewProdutosLocLojaKey[loja.numero].orEmpty()
  }
  
  fun findByAbreviacao(abreviacao: String): List<ViewProdutoLoc> {
    return viewProdutosLocAbreviacaoKey[abreviacao].orEmpty()
  }
  
  fun findByLojaAbreviacao(
    storeno: Int = RegistryUserInfo.lojaDefault.numero, abreviacao: String = abreviacaoDefault
                          ): List<ViewProdutoLoc> {
    return viewProdutosLocLojaAbreviacaoKey[LojaAbreviacaoKey(storeno = storeno, abreviacao = abreviacao)].orEmpty()
  }
}

data class ProdutoKey(val produtoId: Long, val storeno: Int, val abreviacao: String)

data class LojaAbreviacaoKey(val storeno: Int, val abreviacao: String)