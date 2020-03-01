package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.PedidoNotaRessuprimento
import br.com.engecopi.saci.saci

class ETLPedidoNotaRessuprimento: ETL<PedidoNotaRessuprimento>() {
  companion object: ETLThread<PedidoNotaRessuprimento>(ETLPedidoNotaRessuprimento(), 60) {
    override fun getSource() = saci.findPedidoNota()
  }
}