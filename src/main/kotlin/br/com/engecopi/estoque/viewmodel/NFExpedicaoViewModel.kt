package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.Etiqueta
import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.NotaSerie
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.Usuario
import br.com.engecopi.estoque.model.ViewNotaExpedicao
import br.com.engecopi.estoque.model.ViewProdutoLoc
import br.com.engecopi.estoque.model.query.QViewNotaExpedicao
import br.com.engecopi.estoque.ui.log
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.saci.beans.NotaSaci
import java.time.LocalDate
import java.time.LocalTime

class NFExpedicaoViewModel(view: IView): CrudViewModel<ViewNotaExpedicao, QViewNotaExpedicao, NFExpedicaoVo>(view) {
  override fun newBean(): NFExpedicaoVo {
    return NFExpedicaoVo()
  }

  override fun update(bean: NFExpedicaoVo) {
    log?.error("Atualização não permitida")
  }

  override fun add(bean: NFExpedicaoVo) {
    log?.error("Inserssão não permitida")
  }

  override fun delete(bean: NFExpedicaoVo) {
    val nota = bean.findEntity() ?: return
    val saida = Nota.findSaida(nota.numero)
    //TODO Refatorar
    ItemNota.where()
      .nota.equalTo(saida)
      .localizacao.startsWith(bean.abreviacao)
      .delete()
  }

  override val query: QViewNotaExpedicao
    get() = ViewNotaExpedicao.where().let {query ->
      query.loja.id.eq(lojaDefault.id)
        .let {q ->
          if(usuarioDefault.isEstoqueExpedicao) q.abreviacao.eq(abreviacaoDefault).filtroNotaSerie()
          else q
        }
    }

  private fun QViewNotaExpedicao.filtroNotaSerie(): QViewNotaExpedicao {
    val tipos = usuarioDefault.series.map {it.tipoNota}
    val queryOr = or()
    val querySeries = tipos.fold(queryOr) {q, tipo ->
      q.nota.tipoNota.eq(tipo)
    }

    return querySeries.endOr()
  }

  override fun QViewNotaExpedicao.orderQuery(): QViewNotaExpedicao {
    return this.order()
      .lancamento.desc()
      .id.desc()
  }

  override fun ViewNotaExpedicao.toVO(): NFExpedicaoVo {
    val bean = this
    return NFExpedicaoVo().apply {
      numero = bean.numero
      tipoMov = bean.tipoMov
      tipoNota = bean.tipoNota
      rota = bean.rota
      fornecedor = bean.fornecedor
      cliente = bean.cliente
      data = bean.data
      dataEmissao = bean.dataEmissao
      lancamento = bean.lancamento
      hora = bean.hora
      observacao = bean.observacao
      loja = bean.loja
      sequencia = bean.sequencia
      usuario = bean.usuario
      abreviacao = bean.abreviacao
    }
  }

  fun processaKey(key: String, abreviacoes: List<String>) = execValue {
    val notasSaci = findNotaSaidaKey(key)
    if(notasSaci.all {it.isSave()}) throw EViewModel("Todos os itens dessa nota já estão lançados")
    if(notasSaci.isNotEmpty()) {
      val loja = RegistryUserInfo.lojaDefault.numero
      val lojaSaci = notasSaci.firstOrNull()?.storeno ?: 0
      if(loja != lojaSaci) throw EViewModel("Esta nota pertence a loja $lojaSaci")
      val nota: Nota? =
        Nota.createNota(notasSaci.firstOrNull())
          ?.let {
            //TODO Verificar notas já cadastrada
            if(it.existe()) Nota.findSaida(it.numero)
            else {
              it.sequencia = Nota.maxSequencia() + 1
              it.usuario = usuarioDefault
              it.save()
              it
            }
          }
      if(nota == null) throw EViewModel("Nota não encontrada")
      else {
        val itens = notasSaci.mapNotNull {notaSaci ->
          val item = ItemNota.find(notaSaci) ?: ItemNota.createItemNota(notaSaci, nota)
          val abreviacao = item?.abreviacao
          return@mapNotNull if(abreviacoes.contains(abreviacao)) item?.apply {
            status = INCLUIDA
            impresso = false
            usuario = usuarioDefault
            save()
          }
          else null
        }

        if(itens.isEmpty()) throw EViewModel("Essa nota não possui itens com localização")

        crudBean =
          ViewNotaExpedicao.findExpedicao(nota)
            ?.toVO()

        return@execValue nota
      }
    }
    else throw EViewModel("Chave não encontrada")
  }

  private fun imprimir(itemNota: ItemNota?, etiqueta: Etiqueta) = execString {
    itemNota ?: return@execString ""
    val tipoNota = itemNota.tipoNota ?: return@execString ""
    if(!etiqueta.imprimivel(tipoNota)) return@execString ""
    val print = itemNota.printEtiqueta()
    itemNota.let {
      it.refresh()
      it.impresso = true
      it.update()
    }
    print.print(etiqueta.template)
  }

  fun imprimir(nota: Nota?) = execString {
    if(nota == null) ""
    else {
      val id = nota.id
      val notaRef = Nota.byId(id)
      if(notaRef == null) ""
      else {
        val etiquetas = Etiqueta.findByStatus(INCLUIDA)
        val itens = notaRef.itensNota()

        etiquetas.joinToString(separator = "\n") {etiqueta ->
          itens.map {imprimir(it, etiqueta)}
            .distinct()
            .joinToString(separator = "\n")
        }
      }
    }
  }

  fun imprimeTudo() = execString {
    val etiquetas = Etiqueta.findByStatus(INCLUIDA)
    //TODO Refatorar
    val itens =
      ItemNota.where()
        .impresso.eq(false)
        .let {q ->
          if(usuarioDefault.estoque) q.localizacao.startsWith(abreviacaoDefault)
          else q
        }
        .status.eq(INCLUIDA)
        .findList()
    etiquetas.joinToString(separator = "\n") {etiqueta ->
      itens.map {imprimir(it, etiqueta)}
        .distinct()
        .joinToString(separator = "\n")
    }
  }

  fun findNotaSaidaKey(key: String) = execList {
    val notaSaci = when {
      key.length == 44 -> Nota.findNotaSaidaKey(key)
      else             -> Nota.findNotaSaidaSaci(key)
    }.filter {ns ->
      if(usuarioDefault.isEstoqueExpedicao) ViewProdutoLoc.filtraLoc(ns.prdno, ns.grade)
      else true
    }
    if(notaSaci.isEmpty()) throw EViewModel("Chave não encontrada")
    else {
      if(usuarioDefault.isEstoqueExpedicao) {
        val nota = notaSaci.firstOrNull() ?: throw EViewModel("Chave não encontrada")
        val notaSerie = nota.notaSerie() ?: throw EViewModel("Chave não encontrada")
        val tipo = notaSerie.tipoNota
        if(usuarioDefault.isTipoCompativel(tipo)) notaSaci
        else throw EViewModel("O usuário não está habilitado para lançar esse tipo de nota (${notaSerie.descricao})")
      }
      else notaSaci
    }
  }

  fun NotaSaci.notaSerie(): NotaSerie? {
    val tipo = TipoNota.value(tipo)
    return NotaSerie.findByTipo(tipo)
  }

  fun findLoja(storeno: Int?): Loja? = Loja.findLoja(storeno)

  fun abreviacoes(prdno: String?, grade: String?): List<String> {
    val produto = Produto.findProduto(prdno, grade) ?: return emptyList()
    return ViewProdutoLoc.abreviacoesProduto(produto)
  }

  override fun QViewNotaExpedicao.filterString(text: String): QViewNotaExpedicao {
    return nota.numero.startsWith(text)
  }

  override fun QViewNotaExpedicao.filterDate(date: LocalDate): QViewNotaExpedicao {
    return data.eq(date)
  }
}

class NFExpedicaoVo: EntityVo<ViewNotaExpedicao>() {
  override fun findEntity(): ViewNotaExpedicao? {
    return ViewNotaExpedicao.findSaida(numero, abreviacao)
  }

  var numero: String = ""
  var tipoMov: TipoMov = ENTRADA
  var tipoNota: TipoNota? = null
  var rota: String = ""
  var fornecedor: String = ""
  var cliente: String = ""
  var data: LocalDate = LocalDate.now()
  var dataEmissao: LocalDate = LocalDate.now()
  var lancamento: LocalDate = LocalDate.now()
  var hora: LocalTime = LocalTime.now()
  var observacao: String = ""
  var loja: Loja? = null
  var sequencia: Int = 0
  var usuario: Usuario? = null
  var abreviacao: String? = ""
  var impresso: Boolean = false
}