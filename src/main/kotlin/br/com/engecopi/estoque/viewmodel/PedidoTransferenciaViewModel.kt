package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Loja.Find
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.framework.viewmodel.ViewModel
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.saci.saci
import br.com.engecopi.utils.localDate

class PedidoTransferenciaViewModel(view: IView): ViewModel(view) {
  val pedidosTransferencia = mutableListOf<PedidoTransferenciaVo>()

  init {
    refresh()
  }

  fun refresh() = exec {
    pedidosTransferencia.clear()
    val storeno = RegistryUserInfo.lojaDefault.numero
    val notas = saci.findPedidoTransferencia(storeno)
      .filter {pedido -> RegistryUserInfo.abreviacaoDefault in pedido.localizacaoes().map {it.abreviacao}}
      .map {PedidoTransferenciaVo(it)}
    pedidosTransferencia.addAll(notas)
  }
}

class PedidoTransferenciaVo(val notaProdutoSaci: NotaProdutoSaci) {
  val numero = "${notaProdutoSaci.storeno}${notaProdutoSaci.numero}"
  val lojaNF = Loja.findLoja(notaProdutoSaci.storeno)
  val lancamento = notaProdutoSaci.date?.localDate()
  val quantProduto = notaProdutoSaci.quant
  val codigo = notaProdutoSaci.prdno
  val descricaoProduto = Produto.findProduto(notaProdutoSaci.prdno, notaProdutoSaci.grade)
    ?.descricao
  val grade = notaProdutoSaci.grade
  val localizacao = notaProdutoSaci.localizacaoes()
    .filter {loc -> RegistryUserInfo.abreviacaoDefault == loc.abreviacao}
    .joinToString(separator = "/") {it.localizacao}
  val usuario = ""
  val rotaDescricao = notaProdutoSaci.rota
  val cliente = notaProdutoSaci.clienteName
}