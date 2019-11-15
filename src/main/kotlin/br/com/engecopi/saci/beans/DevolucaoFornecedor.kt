package br.com.engecopi.saci.beans

data class DevolucaoFornecedor(val storeno: Int,
                               val pdvno: Int,
                               val xano: String,
                               val invno: Int,
                               val nfeno: String,
                               val nfese: String,
                               val nfsno: String, val nfsse: String) {
  val numeroSerieEntrada
    get() = if(nfese.isBlank()) nfeno else "$nfeno/$nfese"
  val numeroSerieSaida
    get() = if(nfsse.isBlank()) nfsno else "$nfsno/$nfsse"
}