package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.HistoricoEtiqueta
import io.ebean.Finder

open class HistoricoEtiquetaFinder: Finder<Long, HistoricoEtiqueta>(HistoricoEtiqueta::class.java)

