package br.com.engecopi.estoque.viewmodel.expedicao

data class LocalizacaoExpedicao(val abreviacao: String, val itensExpedicao: List<ItemExpedicao>) {
  val countSelecionado
    get() = itensExpedicao.filter { it.selecionado || it.isSave() }.size
}

