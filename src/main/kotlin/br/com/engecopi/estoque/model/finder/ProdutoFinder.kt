package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Produto
import io.ebean.Finder

open class ProdutoFinder: Finder<Long, Produto>(Produto::class.java)


