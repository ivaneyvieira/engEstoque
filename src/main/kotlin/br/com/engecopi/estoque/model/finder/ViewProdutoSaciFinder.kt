package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewProdutoSaci
import io.ebean.Finder

open class ViewProdutoSaciFinder: Finder<String, ViewProdutoSaci>(ViewProdutoSaci::class.java)


