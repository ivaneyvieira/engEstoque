package br.com.engecopi.estoque.viewmodel.ressuprimento

data class LocalizacaoRessuprimento(val abreviacao: String, val itensVendaFutura: List<ItemRessuprimento>) {
  val countSelecionado
    get() = itensVendaFutura.filter {it.selecionado || it.isSave()}.size
}