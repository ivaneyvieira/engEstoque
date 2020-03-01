package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.TabProdutoSaci
import br.com.engecopi.saci.saci

class ETLTabProdutoSaci: ETL<TabProdutoSaci>() {
  companion object: ETLThread<TabProdutoSaci>(ETLTabProdutoSaci(), 30) {
    override fun getSource() = saci.findProdutoSaci()
  }
}
