package br.com.engecopi.estoque.viewmodel.etiquetas

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.HistoricoEtiqueta
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDeposito
import br.com.engecopi.estoque.model.StatusNota
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel

class LabelViewModel(view: ILabelView): ViewModel<ILabelView>(view) {
  fun addFaixaCodigo(codigoI: Int?, codigoF: Int?) = execUnit {
    view.listaProduto = Produto.findFaixaCodigo(codigoI?.toString(), codigoF?.toString())
  }
  
  fun addFaixaNome(nomeI: String?, nomeF: String?) = execUnit {
    view.listaProduto = Produto.findFaixaNome(nomeI, nomeF)
  }
  
  fun addFaixaFabricante(vendno: Int?) = execUnit {
    view.listaProduto = Produto.findFaixaFabricante(vendno)
  }
  
  fun addFaixaCentroLucro(clno: Int?) = execUnit {
    view.listaProduto = Produto.findFaixaCentroLucro(clno)
  }
  
  fun addFaixaTipoProduto(typeno: Int?) = execUnit {
    view.listaProduto = Produto.findTipoProduto(typeno)
  }
  
  fun addFaixaCodigoGrade(codigo: String?, grade: String?) = execUnit {
    val produto = Produto.findProduto(codigo, grade) ?: return@execUnit
    val localizacoes = produto.localizacoes(abreviacaoDefault)
    if(localizacoes.any {it.startsWith(abreviacaoDefault)} && !view.listaProduto.contains(produto)) view.listaProduto += produto
  }
  
  fun clearProduto() = execUnit {
    view.listaProduto = emptyList()
  }
  
  fun pesquisaGrades(codigo: String?): List<String> {
    return Produto.findProdutos(codigo)
      .map {it.grade}
  }
  
  fun impressaoProduto(): String? {
    val etiquetas = Etiqueta.findByStatus(StatusNota.PRODUTO, "")
    val template = etiquetas.joinToString(separator = "\n") {it.template}
    
    return view.listaProduto.joinToString(separator = "\n") {prd ->
      val barcodeGtin = prd.barcodeGtin.distinct()
      barcodeGtin.joinToString(separator = "\n") {bar ->
        val print =
          template.replace("[Codigo]", prd.codigo.trim())
            .replace("[Grade]", prd.grade)
            .replace("[Descricao]", prd.descricao ?: "")
            .replace("[Gtin]", bar)
        HistoricoEtiqueta.save(prd, bar, print)
        print
      }
    }
  }
  
  fun addFaixaNfe(nfe: String?) = execUnit {
    val produtos =
      Nota.findNotaEntradaSaci(lojaDeposito, nfe)
        .mapNotNull {
          Produto.findProduto(it.prdno, it.grade)
        }
    view.listaProduto = produtos
  }
}

interface ILabelView: IView {
  var listaProduto: List<Produto>
}