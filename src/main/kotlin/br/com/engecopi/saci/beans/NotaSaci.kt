package br.com.engecopi.saci.beans

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewProdutoLoc

class NotaSaci(val rota: String?,
               val storeno: Int?,
               val numero: String?,
               val serie: String?,
               val date: Int?,
               val dt_emissao: Int?,
               val prdno: String?,
               val grade: String?,
               val quant: Int?,
               val vendName: String? = "",
               val clienteName: String? = "",
               val tipo: String?,
               val invno: Int?) {
  fun isSave(): Boolean {
    return ItemNota.isSave(this)
  }

  fun numeroSerie(): String {
    numero ?: return ""
    return if(serie.isNullOrBlank()) numero
    else "$numero/$serie"
  }

  fun localizacaoes(): List<ViewProdutoLoc> {
    return ViewProdutoLoc.findByCodigoGrade(prdno, grade)
  }

  fun tipoNota(): TipoNota? = TipoNota.value(tipo)
}