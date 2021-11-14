package br.com.engecopi.estoque.viewmodel.retiraFutura

data class LocalizacaoFutura(val abreviacao: String, val itensChaveFutura: List<ItemChaveFutura>) {
  val countSelecionado
    get() = itensChaveFutura.filter {it.selecionado || it.isSave()}.size
}