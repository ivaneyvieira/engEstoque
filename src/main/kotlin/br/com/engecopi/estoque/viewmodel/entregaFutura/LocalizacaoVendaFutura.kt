package br.com.engecopi.estoque.viewmodel.entregaFutura

data class LocalizacaoVendaFutura(val abreviacao: String, val itensEntregaFutura: List<ItemEntregaFutura>) {
  val countSelecionado
    get() = itensEntregaFutura.filter {it.selecionado || it.isSave()}.size
}