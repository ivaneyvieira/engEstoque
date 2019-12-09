package br.com.engecopi.estoque.viewmodel.entregaFutura

data class LocalizacaoVendaFutura(val abreviacao: String, val itensVendaFutura: List<ItemVendaFutura>) {
  val countSelecionado
    get() = itensVendaFutura.filter {it.selecionado || it.isSave()}.size
}