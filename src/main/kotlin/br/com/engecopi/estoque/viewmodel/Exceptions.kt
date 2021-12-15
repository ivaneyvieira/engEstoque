package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.framework.viewmodel.EViewModelError

class EChaveNaoEncontrada: EViewModelError("Chave não encontrada")

class ENotaNaoEntregaFutura(val numero: String): EViewModelError("A nota $numero não é uma fatura de entrega futura")

class ENotaNaoRetiraFutura(val numero: String): EViewModelError("A nota $numero não é uma fatura de retira futura")

class ENotaEntregaFutura(val numero: String): EViewModelError("A nota $numero é uma fatura de entrega futura")

class EPedidoRessuprimento(val numero: String): EViewModelError("A nota $numero é um pedido de ressuprimento")

class EPedidoAbastecimento(val numero: String): EViewModelError("A nota $numero é um pedido de abastecimento")

class ENFKeyInvalido(val key: String): EViewModelError("'$key' não é uma chave de nota válida")

class ENovaFaturaLancada(): EViewModelError("Não pode usar nota já faturada como entrega futura")

class ENovaBaixaLancada(): EViewModelError("Não pode usar nota já faturada na expedição")

class ENaoAbastecimento(val numero: String): EViewModelError("A chave $numero não é uma nota de abstecimento")
