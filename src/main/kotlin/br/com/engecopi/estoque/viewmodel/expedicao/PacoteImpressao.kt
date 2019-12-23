package br.com.engecopi.estoque.viewmodel.expedicao

import br.com.engecopi.estoque.model.envelopes.Printer

data class PacoteImpressao(val impressora: Printer, val text: String)