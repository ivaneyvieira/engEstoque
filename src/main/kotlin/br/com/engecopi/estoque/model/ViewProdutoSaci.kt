package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.finder.ViewProdutoSaciFinder
import br.com.engecopi.estoque.model.query.QViewProdutoSaci
import br.com.engecopi.utils.lpad
import io.ebean.annotation.Cache
import io.ebean.annotation.View
import javax.persistence.Entity
import javax.persistence.Id

@Cache(enableQueryCache = false)
@Entity
@View(name = "v_produtos_saci")
class ViewProdutoSaci {
  @Id
  var id: String = ""
  var codigo: String? = null
  var nome: String? = null
  var grade: String? = null
  var codebar: String? = null
  var custo: Double? = null
  var unidade: String? = null
  var tipo: String? = null
  
  companion object Find: ViewProdutoSaciFinder() {
    fun find(codigo: String?, grade: String?): ViewProdutoSaci? {
      codigo ?: return null
      val gradeN = grade ?: ""
      return QViewProdutoSaci().codigo.eq(codigo.lpad(16, " "))
        .grade.eq(gradeN)
        .findList()
        .firstOrNull()
    }
    
    fun find(codigo: String?): List<ViewProdutoSaci> {
      codigo ?: return emptyList()
      return QViewProdutoSaci().codigo.eq(codigo.lpad(16, " "))
        .findList()
    }
    
    fun existe(codigo: String?): Boolean {
      codigo ?: return false
      return QViewProdutoSaci().codigo.eq(codigo.lpad(16, " "))
        .exists()
    }
    
    fun temGrade(codigo: String?): Boolean {
      codigo ?: return false
      return QViewProdutoSaci().codigo.eq(codigo.lpad(16, " ")).grade.ne("").findCount() > 0
    }
  }
}
