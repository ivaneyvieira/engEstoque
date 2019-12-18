package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewProdutoLoc
import io.ebean.Finder

open class ViewProdutoLocFinder: Finder<String, ViewProdutoLoc>(ViewProdutoLoc::class.java)


