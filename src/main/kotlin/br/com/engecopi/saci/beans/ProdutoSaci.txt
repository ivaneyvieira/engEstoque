package br.com.engecopi.saci.beans

class ProdutoSaci(
        val codigo: String?,
        val nome: String?,
        val grade: String?,
        val codebar: String?,
        val custo: Double?,
        val unidade : String?,
        val tipo : String?
                 ){
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    
    other as ProdutoSaci
    
    if (codigo != other.codigo) return false
    if (grade != other.grade) return false
    
    return true
  }
  
  override fun hashCode(): Int {
    var result = codigo?.hashCode() ?: 0
    result = 31 * result + (grade?.hashCode() ?: 0)
    return result
  }
}