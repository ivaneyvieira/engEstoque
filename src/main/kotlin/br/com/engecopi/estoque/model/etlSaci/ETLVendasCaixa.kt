package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.VendasCaixa
import br.com.engecopi.saci.saci

class ETLVendasCaixa: ETL<VendasCaixa>() {
  companion object: ETLThread<VendasCaixa>(ETLVendasCaixa(), 30) {
    override fun getSource() = saci.findVendasCaixa()
  }
}
