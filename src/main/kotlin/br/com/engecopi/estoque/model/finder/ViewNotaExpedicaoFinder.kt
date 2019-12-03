package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewNotaExpedicao
import io.ebean.Finder

open class ViewNotaExpedicaoFinder: Finder<Long, ViewNotaExpedicao>(ViewNotaExpedicao::class.java)

