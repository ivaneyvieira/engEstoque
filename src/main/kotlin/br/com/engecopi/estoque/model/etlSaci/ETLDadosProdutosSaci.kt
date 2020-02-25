package br.com.engecopi.estoque.model.etlSaci

import br.com.engecopi.estoque.model.dtos.DadosProdutosSaci
import br.com.engecopi.saci.saci

class ETLDadosProdutosSaci: ETL<DadosProdutosSaci>() {
  companion object: ETLThread<DadosProdutosSaci>(ETLDadosProdutosSaci(), 60) {
    override fun getSource() = saci.findDadosProdutosSaci()
  }
}
