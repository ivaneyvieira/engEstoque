package br.com.engecopi.estoque.viewmodel.ressuprimento

data class LocalizacaoRessuprimento(val abreviacao: String, val itensRessuprimento: List<ItemRessuprimento>) {
  val countSelecionado
    get() = itensRessuprimento.filter { it.selecionado || it.isSave() }.size
}