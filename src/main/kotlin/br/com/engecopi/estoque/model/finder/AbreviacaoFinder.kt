package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Abreviacao
import io.ebean.Finder

open class AbreviacaoFinder: Finder<Long, Abreviacao>(Abreviacao::class.java)


