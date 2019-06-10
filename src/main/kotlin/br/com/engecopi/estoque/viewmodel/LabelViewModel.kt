package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.ui.views.LabelView
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.framework.viewmodel.ViewModel

class LabelViewModel(view: LabelView): ViewModel(view) {
  val template = """
^XA
^FT20,50^A0N,30,20^FH\^FD[Produto]^FS
^FT20,200^BY2^BCN,80,Y,N,N^FD[GTIN]^FS
^XZ
  """.trimIndent()
  var codigo: String = ""
  var grade: String? = ""
  var listGrade = emptyList<String>()
  var descricao: String = ""
  var gtin : String = ""

  fun pesquisaCodigo() = exec {
    val produtos = Produto.findProdutos(codigo)
    val produto = produtos.firstOrNull() ?: throw  EViewModel("Produto não encontrado")
    descricao = produto.descricao ?: ""
    listGrade = produtos.map {it.grade}
    grade = if(listGrade.contains(grade)) grade
    else listGrade.firstOrNull()
    gtin = Produto.findProduto(codigo, grade)?.findBarcode() ?: ""
  }

  fun templateLabel() = execValue {
    val produto = Produto.findProduto(codigo, grade) ?: throw  EViewModel("Produto não encontrado")
    val barcode = produto.findBarcode() ?: return@execValue null
    val idProduto = if(grade == "") "$codigo $descricao" else "$codigo-$grade $descricao"
    template.replace("[Produto]", idProduto)
      .replace("[GTIN]", barcode)
  }
}