package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.ui.views.LabelView
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.framework.viewmodel.ViewModel

class LabelViewModel(view: LabelView): ViewModel(view) {
  val template = """
^XA
^FT20,50^A0N,30,20^FH\^FD[Codigo] - [Grade]^FS
^FT20,100^A0N,30,20^FH\^FD[Descricao]^FS
^^FT20,200^BY2^BCN,50,Y,N,N^FD[GTIN]^FS
^XZ
  """.trimIndent()
  var codigo: String = ""
  var grade: String = ""
  var listGrade = emptyList<String>()
  var descricao: String = ""

  fun pesquisaCodigo() = exec {
    val produtos = Produto.findProdutos(codigo)
    val produto = produtos.firstOrNull() ?: throw  EViewModel("Produto não encontrado")
    descricao = produto.descricao ?: ""
    listGrade = produtos.map {it.grade}
    grade = produto.grade
  }

  fun getCodBar() = execString {
    val produto = Produto.findProduto(codigo, grade) ?: throw  EViewModel("Produto não encontrado")
    val barcode = produto.findBarcode() ?: return@execString ""
    template.replace("[Codigo]", codigo)
      .replace("[Grade]", grade)
      .replace("[Descricao]", descricao)
      .replace("[GTIN]", barcode)
  }
}