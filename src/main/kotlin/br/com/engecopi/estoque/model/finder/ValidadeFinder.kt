package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Validade
import io.ebean.Finder

open class ValidadeFinder : Finder<Long, Validade>(Validade::class.java)


