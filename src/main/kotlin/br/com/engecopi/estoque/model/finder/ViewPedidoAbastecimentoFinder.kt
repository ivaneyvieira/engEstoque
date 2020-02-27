package br.com.engecopi.estoque.model.finder

import br.com.engecopi.estoque.model.ViewPedidoAbastecimento
import io.ebean.Finder

open class ViewPedidoAbastecimentoFinder : Finder<Long, ViewPedidoAbastecimento>(ViewPedidoAbastecimento::class.java)


