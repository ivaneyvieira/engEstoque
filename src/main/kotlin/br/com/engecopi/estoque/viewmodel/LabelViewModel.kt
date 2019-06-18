package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.estoque.ui.views.LabelView
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.framework.viewmodel.ViewModel

class LabelViewModel(view: LabelView): ViewModel(view) {
  val listaProduto = arrayListOf<Produto>()

  fun addFaixaCodigo(codigoI: Int?, codigoF: Int?) = exec {
    listaProduto.clear()
    val produtos = Produto.findFaixaCodigo(codigoI?.toString(), codigoF?.toString())
    listaProduto.addAll(produtos)
  }

  fun addFaixaNome(nomeI: String?, nomeF: String?) = exec {
    listaProduto.clear()
    val produtos = Produto.findFaixaNome(nomeI, nomeF)
    listaProduto.addAll(produtos)
  }

  fun addFaixaFabricante(vendno: Int?) = exec {
    listaProduto.clear()
    val produtos = Produto.findFaixaFabricante(vendno)
    listaProduto.addAll(produtos)
  }

  fun addFaixaCentroLucro(clno: Int?) = exec {
    listaProduto.clear()
    val produtos = Produto.findFaixaCentroLucro(clno)
    listaProduto.addAll(produtos)
  }

  fun addFaixaTipoProduto(typeno: Int?) = exec {
    listaProduto.clear()
    val produtos = Produto.findTipoProduto(typeno)
    listaProduto.addAll(produtos)
  }

  fun addFaixaCodigoGrade(codigo: String?, grade: String?) = exec {
    val produto = Produto.findProduto(codigo, grade) ?: throw EViewModel("Produto não localizado")
    if(!listaProduto.contains(produto)) listaProduto.add(produto)
  }

  fun clearProduto() = exec {
    listaProduto.clear()
  }

  fun pesquisaGrades(codigo: String?): List<String> {
    return Produto.findProdutos(codigo)
      .map {it.grade}
  }

  fun impressao(): String? {
    val etiquetas = Etiqueta.findByStatus(StatusNota.PRODUTO)
    val template = etiquetas.joinToString(separator = "\n") {it.template}

    return listaProduto.filter {!it.barcodeGtin.isNullOrBlank()}
      .joinToString(separator = "\n") {
        template.replace("[Codigo]", it.codigo.trim())
          .replace("[Grade]", it.grade)
          .replace("[Descricao]", it.descricao ?: "")
          .replace("[Gtin]", it.barcodeGtin ?: "")
      }
  }

  fun addFaixaNfe(nfe: String?) = exec {
    listaProduto.clear()
    val produtos = Nota.findNotaEntradaSaci(nfe)
      .mapNotNull {
        Produto.findProduto(it.prdno, it.grade)
      }
    listaProduto.addAll(produtos)
  }
}