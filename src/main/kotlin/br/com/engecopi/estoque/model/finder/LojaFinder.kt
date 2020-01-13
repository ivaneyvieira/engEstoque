package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Loja
import io.ebean.Finder

open class LojaFinder : Finder<Long, Loja>(Loja::class.java)


