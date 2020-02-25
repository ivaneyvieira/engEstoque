package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.TransferenciaAutomatica
import br.com.engecopi.saci.saci

class ETLTransferenciaAutomatica: ETL<TransferenciaAutomatica>() {
  companion object: ETLThread<TransferenciaAutomatica>(ETLTransferenciaAutomatica(), 60) {
    override fun getSource() = saci.findTransferenciaAutomatica()
  }
}