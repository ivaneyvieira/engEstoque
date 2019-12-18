package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Nota
import io.ebean.Finder

open class NotaFinder: Finder<Long, Nota>(Nota::class.java)


