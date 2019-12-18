package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewCodBarCliente
import io.ebean.Finder

open class ViewCodBarClienteFinder: Finder<Long, ViewCodBarCliente>(ViewCodBarCliente::class.java)


