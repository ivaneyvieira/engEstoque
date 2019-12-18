package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Etiqueta
import io.ebean.Finder

open class EtiquetaFinder: Finder<Long, Etiqueta>(Etiqueta::class.java)


