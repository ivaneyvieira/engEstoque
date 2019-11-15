package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.HistoricoEtiqueta
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel

class LabelViewModel(view: ILabelView): ViewModel<ILabelView>(view) {
  fun addFaixaCodigo(codigoI: Int?, codigoF: Int?) = exec {
    view.listaProduto = Produto.findFaixaCodigo(codigoI?.toString(), codigoF?.toString())
  }

  fun addFaixaNome(nomeI: String?, nomeF: String?) = exec {
    view.listaProduto = Produto.findFaixaNome(nomeI, nomeF)
  }

  fun addFaixaFabricante(vendno: Int?) = exec {
    view.listaProduto = Produto.findFaixaFabricante(vendno)
  }

  fun addFaixaCentroLucro(clno: Int?) = exec {
    view.listaProduto = Produto.findFaixaCentroLucro(clno)
  }

  fun addFaixaTipoProduto(typeno: Int?) = exec {
    view.listaProduto = Produto.findTipoProduto(typeno)
  }

  fun addFaixaCodigoGrade(codigo: String?, grade: String?) = exec {
    val produto = Produto.findProduto(codigo, grade) ?: return@exec
    val localizacoes = produto.localizacoes(RegistryUserInfo.abreviacaoDefault)
    if(localizacoes.any {it.startsWith(RegistryUserInfo.abreviacaoDefault)} && !view.listaProduto.contains(produto)) view.listaProduto += produto
  }

  fun clearProduto() = exec {
    view.listaProduto = emptyList()
  }

  fun pesquisaGrades(codigo: String?): List<String> {
    return Produto.findProdutos(codigo).map {it.grade}
  }

  fun impressao(): String? {
    val etiquetas = Etiqueta.findByStatus(StatusNota.PRODUTO)
    val template = etiquetas.joinToString(separator = "\n") {it.template}

    return view.listaProduto.joinToString(separator = "\n") {prd ->
      val barcodeGtin = prd.barcodeGtin.distinct()
      barcodeGtin.joinToString(separator = "\n") {bar ->
        val print = template.replace("[Codigo]", prd.codigo.trim()).replace("[Grade]", prd.grade)
          .replace("[Descricao]", prd.descricao ?: "").replace("[Gtin]", bar)
        HistoricoEtiqueta.save(prd, bar, print)
        print
      }
    }
  }

  fun addFaixaNfe(nfe: String?) = exec {
    val produtos = Nota.findNotaEntradaSaci(nfe).mapNotNull {
      Produto.findProduto(it.prdno, it.grade)
    }
    view.listaProduto = produtos
  }
}

interface ILabelView: IView {
  var listaProduto: List<Produto>
}