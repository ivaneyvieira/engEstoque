package br.com.engecopi.estoque.viewmodel

import br.com.engecopi.estoque.model.ItemNota
import br.com.engecopi.estoque.model.LocProduto
import br.com.engecopi.estoque.model.Loja
import br.com.engecopi.estoque.model.Produto
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.userDefaultIsAdmin
import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.Repositories
import br.com.engecopi.estoque.model.TipoNota
import br.com.engecopi.estoque.model.ViewProdutoSaci
import br.com.engecopi.estoque.model.query.QProduto
import br.com.engecopi.framework.viewmodel.CrudViewModel
import br.com.engecopi.framework.viewmodel.EViewModel
import br.com.engecopi.framework.viewmodel.EntityVo
import br.com.engecopi.framework.viewmodel.IView
import br.com.engecopi.utils.lpad
import java.time.LocalDate

class ProdutoViewModel(view: IView): CrudViewModel<Produto, QProduto, ProdutoVo>(view) {
  override fun newBean(): ProdutoVo {
    val bean = crudBean ?: ProdutoVo()
    return bean.apply {
      bean.codigoProduto = ""
    }
  }

  override fun update(bean: ProdutoVo) {
    bean.toEntity()
      ?.let {produto ->
        produto.codigo = bean.codigoProduto.lpad(16, " ")
        produto.codebar = bean.codebar ?: ""
        produto.update()
      }
  }

  override fun add(bean: ProdutoVo) {
    Produto().apply {
      val gradesSalvas = Produto.findProdutos(bean.codigoProduto)
        .map {it.grade}
      if(!ViewProdutoSaci.existe(bean.codigoProduto)) throw EViewModel("Este produto não existe")
      if(ViewProdutoSaci.temGrade(bean.codigoProduto)) {
        val gradesProduto = bean.gradesProduto.filter {it != ""}
        if(gradesProduto.isEmpty()) throw EViewModel("Este produto deveria tem grade")
        else gradesProduto.filter {grade -> !gradesSalvas.contains(grade)}.forEach {grade ->
          this.codigo = bean.codigoProduto.lpad(16, " ")
          this.grade = grade
          this.codebar = bean.codebar ?: ""
          this.save()
        }
      }
      else {
        this.codigo = bean.codigoProduto.lpad(16, " ")
        this.grade = ""
        this.codebar = bean.codebar ?: ""
        this.save()
      }
    }
    bean.codigoProduto = ""
  }

  override fun delete(bean: ProdutoVo) {
    Produto.findProdutos(bean.codigoProduto, bean.gradesProduto.toList())
      .forEach {it.delete()}
  }

  private fun QProduto.filtroUsuario(): QProduto {
    return this.viewProdutoLoc.localizacao.startsWith(abreviacaoDefault)
      .viewProdutoLoc.loja.id.eq(lojaDefault.id)
  }

  override val query: QProduto
    get() {
      Repositories.updateViewProdutosLoc()
      return Produto.where()
        .filtroUsuario()
    }

  override fun Produto.toVO(): ProdutoVo {
    val produto = this
    return ProdutoVo().apply {
      entityVo = produto
      codigoProduto = produto.codigo.trim()
      //gradesProduto = Produto.findGradesProduto(produto.codigo).toSet()
      lojaDefault = usuarioDefault.loja
    }
  }

  override fun QProduto.filterString(text: String): QProduto {
    return codigo.contains(text)
      .codebar.eq(text)
      .vproduto.nome.contains(text)
      .grade.contains(text)
      .localizacao.contains(text)
  }

  fun localizacoes(bean: ProdutoVo?): List<LocProduto> {
    return bean?.produto?.localizacoes(abreviacaoDefault)
      .orEmpty()
      .filter {it.startsWith(abreviacaoDefault) || userDefaultIsAdmin}
      .map {LocProduto(it)}
  }

  fun saveItem(item: ItemNota?) {
    item?.save()
  }
}

class ProdutoVo: EntityVo<Produto>() {
  override fun findEntity(): Produto? {
    return Produto.findProdutos(codigoProduto)
      .firstOrNull()
  }

  var lojaDefault: Loja? = null
  var codigoProduto: String? = ""
    set(value) {
      field = value
      if(entityVo == null) gradesProduto = Produto.findGradesProduto(value)
        .toSet()
    }
  var gradesProduto: Set<String> = emptySet()
  val descricaoProduto: String?
    get() = produto?.descricao
  val descricaoProdutoSaci: String?
    get() = if(entityVo == null) ViewProdutoSaci.find(codigoProduto).firstOrNull()?.nome else entityVo?.descricao
  val grades
    get() = if(entityVo == null) ViewProdutoSaci.find(codigoProduto).mapNotNull {it.grade}
    else listOf(entityVo?.grade ?: "")
  val codebar: String?
    get() = produto?.codebar ?: ""
  val localizacao
    get() = produto?.localizacoes(abreviacaoDefault).orEmpty().filter {
      it.startsWith(abreviacaoDefault)
    }.asSequence().distinct().joinToString(" / ")
  val produto
    get() = toEntity()
  val temGrade get() = toEntity()?.temGrade
  val grade get() = produto?.grade ?: ""
  val saldo
    get() = produto?.saldoAbreviacao(abreviacaoDefault) ?: 0
  val comprimento: Int?
    get() = produto?.vproduto?.comp
  val lagura: Int?
    get() = produto?.vproduto?.larg
  val altura: Int?
    get() = produto?.vproduto?.alt
  val cubagem: Double?
    get() = produto?.vproduto?.cubagem
  var filtroDI: LocalDate? = null
  var filtroDF: LocalDate? = null
  var filtroTipo: TipoNota? = null
  var filtroLocalizacao: LocProduto? = null
  val itensNota: List<ItemNota>
    get() {
      produto?.recalculaSaldos()

      return produto?.findItensNota()
        .orEmpty()
        .asSequence()
        .filter {item ->
          (lojaDefault?.let {lDef -> item.nota?.loja?.id == lDef.id} ?: true) && (filtroDI?.let {di ->
            (item.nota?.data?.isAfter(di) ?: true) || (item.nota?.data?.isEqual(di) ?: true)
          } ?: true) && (filtroDF?.let {df ->
            (item.nota?.data?.isBefore(df) ?: true) || (item.nota?.data?.isEqual(df) ?: true)
          } ?: true) && (filtroTipo?.let {t -> item.nota?.tipoNota == t}
                         ?: true) && (filtroLocalizacao?.let {loc -> item.localizacao == loc.localizacao}
                                      ?: true) && (item.quantidadeSaldo != 0)
        }
        .sortedWith(compareBy(ItemNota::localizacao, ItemNota::data, ItemNota::hora))
        .toList()
    }
}
