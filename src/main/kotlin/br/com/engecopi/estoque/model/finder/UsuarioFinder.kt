package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.Usuario
import io.ebean.Finder

open class UsuarioFinder : Finder<Long, Usuario>(Usuario::class.java)


