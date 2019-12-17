package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.framework.viewmodel.EViewModelError

class EChaveNaoEncontrada: EViewModelError("Chave não encontrada")

class ENotaNaoEntregaFutura(val numero: String): EViewModelError("A nota $numero não é uma fatura de entrega futura")

class ENotaEntregaFutura(val numero: String): EViewModelError("A nota $numero é uma fatura de entrega futura")

class ENFKeyInvalido(val key: String): EViewModelError("'$key' não é uma chave de nota válida")

class ENovaBaixa(): EViewModelError("Não pode usar nota de baixa na expedição")

