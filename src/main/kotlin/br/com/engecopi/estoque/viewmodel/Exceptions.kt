package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.framework.viewmodel.EViewModel

class EChaveNaoEncontrada : EViewModel("Chave não encontrada")

class ENotaNaoEntregaFutura(val numero : String): EViewModel("A nota $numero não é uma fatura de entrega futura")

class ENotaEntregaFutura(val numero : String): EViewModel("A nota $numero é uma fatura de entrega futura")

