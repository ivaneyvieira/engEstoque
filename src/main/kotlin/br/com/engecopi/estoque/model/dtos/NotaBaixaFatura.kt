package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.KeyNota
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.saci.beans.NotaProdutoSaci
import java.time.LocalDate

data class NotaBaixaFatura(val storeno: Int,
                           val numero: String,
                           val data: LocalDate?) {
  fun keyNota(): KeyNota? {
    return if(numero == "0" || numero == "") null
    else KeyNota("$storeno$numero")
  }
  
  val produtos = ProdutoNota.produtos(storeno, numero)
}

val List<NotaBaixaFatura>.storeno
  get() = this.map {it.storeno}.distinct().joinToString(separator = "/")
val List<NotaBaixaFatura>.data
  get() = this.mapNotNull {it.data}.max()

data class ProdutoNota(val prdno: String?, val grade: String?) {
  companion object {
    fun produtos(storeno: Int, numero: String) =
      Nota.findNotaSaidaSaci(storeno, numero).map {produto -> produto.toProdutoNota()}
  }
}

fun NotaProdutoSaci.toProdutoNota() = ProdutoNota(prdno, grade)

fun List<NotaBaixaFatura>.filterProduto(prdno: String?, grade: String?): List<NotaBaixaFatura> {
  return this.filter {it.produtos.contains(ProdutoNota(prdno, grade))}
}