package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewNotaFutura
import io.ebean.Finder

open class ViewNotaFuturaFinder: Finder<Long, ViewNotaFutura>(ViewNotaFutura::class.java)

