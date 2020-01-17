package br.com.engecopi.estoque.model.dtos

import br.com.engecopi.estoque.model.etlSaci.ETLDevolucaoFornecedor
import br.com.engecopi.estoque.model.etlSaci.EntryID
import io.ebean.DB

class DevolucaoFornecedor(id: String,
                          val storeno: Int,
                          val pdvno: Int,
                          val xano: String,
                          val invno: Int,
                          val nfeno: String,
                          val nfese: String,
                          val nfsno: String,
                          val nfsse: String,
                          val localizacao: String): EntryID(id) {
  val numeroSerieEntrada
    get() = if(nfese.isBlank()) nfeno else "$nfeno/$nfese"
  val numeroSerieSaida
    get() = if(nfsse.isBlank()) nfsno else "$nfsno/$nfsse"
  override val chave: String
    get() = "$nfeno:$nfese:$nfsno:$nfsse"
  
  companion object {
    fun findByAbreviacao(abreviacao: String) =
      DB.findDto(DevolucaoFornecedor::class.java,
                 ETLDevolucaoFornecedor.sql).findList()
        .filter {it.localizacao.startsWith(abreviacao)}
  }
}
