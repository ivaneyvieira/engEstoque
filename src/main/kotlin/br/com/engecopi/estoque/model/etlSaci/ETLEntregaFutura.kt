package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.EntregaFutura
import br.com.engecopi.saci.saci

class ETLEntregaFutura: ETL<EntregaFutura>() {
  companion object: ETLThread<EntregaFutura>(ETLEntregaFutura(), 60) {
    override fun getSource() = saci.findEntregaFutura()
  }
}