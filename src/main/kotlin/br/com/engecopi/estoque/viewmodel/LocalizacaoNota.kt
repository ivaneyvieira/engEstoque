package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.viewmodel.entregaFutura.ItemVendaFutura

data class LocalizacaoNota(val abreviacao: String, val itensExpedicao: List<ItemExpedicao>) {
  val countSelecionado
    get() = itensExpedicao.filter {it.selecionado || it.isSave()}.size
}

data class LocalizacaoVendaFutura(val abreviacao: String, val itensVendaFutura: List<ItemVendaFutura>) {
  val countSelecionado
    get() = itensVendaFutura.filter {it.selecionado || it.isSave()}.size
}