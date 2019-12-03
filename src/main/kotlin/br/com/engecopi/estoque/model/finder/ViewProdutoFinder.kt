package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewProduto
import io.ebean.Finder

open class ViewProdutoFinder: Finder<Long, ViewProduto>(ViewProduto::class.java)

