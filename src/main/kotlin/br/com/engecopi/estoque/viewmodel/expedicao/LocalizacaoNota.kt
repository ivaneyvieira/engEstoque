package br.com.engecopi.estoque.viewmodel.expedicao

data class LocalizacaoNota(val abreviacao: String, val itensExpedicao: List<ItemExpedicao>) {
  val countSelecionado
    get() = itensExpedicao.filter {it.selecionado || it.isSave()}.size
}

