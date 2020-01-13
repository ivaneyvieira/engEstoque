package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ItemNota
import io.ebean.Finder

open class ItemNotaFinder : Finder<Long, ItemNota>(ItemNota::class.java)


