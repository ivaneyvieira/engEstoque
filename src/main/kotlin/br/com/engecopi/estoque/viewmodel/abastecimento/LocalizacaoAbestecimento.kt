package br.com.engecopi.estoque.viewmodel.abastecimento

data class LocalizacaoAbestecimento(val abreviacao: String, val itensAbastecimento: List<ItemAbastecimento>) {
  val countSelecionado
    get() = itensAbastecimento.filter {it.selecionado || it.isSave()}.size
}

