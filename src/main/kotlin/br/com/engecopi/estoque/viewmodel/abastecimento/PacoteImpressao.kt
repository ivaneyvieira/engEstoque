package br.com.engecopi.estoque.viewmodel.abastecimento

import br.com.engecopi.estoque.model.envelopes.Printer

data class PacoteImpressao(val impressora: Printer, val text: String)