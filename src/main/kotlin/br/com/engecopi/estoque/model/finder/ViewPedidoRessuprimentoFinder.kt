package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewPedidoRessuprimento
import io.ebean.Finder

open class ViewPedidoRessuprimentoFinder : Finder<Long, ViewPedidoRessuprimento>(ViewPedidoRessuprimento::class.java)


