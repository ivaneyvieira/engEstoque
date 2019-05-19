package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.lojaDefault
import br.com.engecopi.estoque.model.StatusNota.CONFERIDA
import br.com.engecopi.estoque.model.StatusNota.ENTREGUE
import br.com.engecopi.estoque.model.StatusNota.ENT_LOJA
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.finder.ItemNotaFinder
import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.utils.format
import io.ebean.annotation.Cache
import io.ebean.annotation.CacheQueryTuning
import io.ebean.annotation.Index
import io.ebean.annotation.Length
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Transient
import javax.validation.constraints.Size
import kotlin.reflect.full.memberProperties

@Entity
@Cache(enableQueryCache = true)
@CacheQueryTuning(maxSecsToLive = 30)
@Table(name = "itens_nota")
@Index(unique = true, columnNames = ["nota_id", "produto_id", "localizacao"])
class ItemNota: BaseModel() {
  var data: LocalDate = LocalDate.now()
  var hora: LocalTime = LocalTime.now()
  var quantidade: Int = 0
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var produto: Produto? = null
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var nota: Nota? = null
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var etiqueta: Etiqueta? = null
  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var usuario: Usuario? = null
  var saldo: Int? = 0
  var impresso: Boolean = false
  @Length(60)
  var localizacao: String = ""
  @Enumerated(EnumType.STRING)
  var status: StatusNota = ENTREGUE
  @Size(max = 60)
  var codigoBarraCliente: String? = ""
  @Size(max = 60)
  var codigoBarraConferencia: String? = ""
  @Size(max = 60)
  var codigoBarraEntrega: String? = ""
  val quantidadeSaldo: Int
    get() = (status.multiplicador) * quantidade * (nota?.multipicadorCancelado ?: 0)
  val viewCodigoBarraConferencia: ViewCodBarConferencia?
    @Transient get() = ViewCodBarConferencia.byId(id)
  val viewCodigoBarraCliente: ViewCodBarCliente?
    @Transient get() = ViewCodBarCliente.byId(nota?.id)
  val viewCodigoBarraEntrega: ViewCodBarEntrega?
    @Transient get() = ViewCodBarEntrega.byId(id)
  val descricao: String?
    @Transient get() = produto?.descricao
  val codigo: String?
    @Transient get() = produto?.codigo
  val grade: String?
    @Transient get() = produto?.grade
  val numeroNota: String?
    @Transient get() = nota?.numero
  val rota: String?
    @Transient get() = nota?.rota
  val tipoMov: TipoMov?
    @Transient get() = nota?.tipoMov
  val tipoNota: TipoNota?
    @Transient get() = nota?.tipoNota
  val dataNota: LocalDate?
    @Transient get() = nota?.data
  val ultilmaMovimentacao: Boolean
    @Transient get() {
      return produto?.ultimaNota()?.let {
        it.id == this.id
      } ?: true
    }
  val etiquetas: List<Etiqueta>
    @Transient get() = Etiqueta.findByStatus(status)
  val abreviacao: String
    @Transient get() = localizacao.split('.').getOrNull(0) ?: ""

  companion object Find: ItemNotaFinder() {
    fun find(nota: Nota?, produto: Produto?): ItemNota? {
      nota ?: return null
      produto ?: return null
      return where().fetchQuery("nota")
        .nota.id.eq(nota.id)
        .produto.id.eq(produto.id)
        .findList()
        .firstOrNull()
    }

    fun find(notaSaci: NotaSaci?): ItemNota? {
      notaSaci ?: return null
      val produtoSaci = Produto.findProduto(notaSaci.prdno, notaSaci.grade) ?: return null
      return where().fetchQuery("nota")
        .nota.numero.eq("${notaSaci.numero}/${notaSaci.serie}")
        .nota.loja.equalTo(RegistryUserInfo.lojaDefault)
        .produto.equalTo(produtoSaci)
        .findList()
        .firstOrNull()
    }

    fun createItemNota(notaSaci: NotaSaci, notaPrd: Nota?): ItemNota? {
      notaPrd ?: return null
      val produtoSaci = Produto.findProduto(notaSaci.prdno, notaSaci.grade) ?: return null
      val locProduto = ViewProdutoLoc.localizacoesProduto(produtoSaci).firstOrNull() ?: ""

      return ItemNota().apply {
        quantidade = notaSaci.quant ?: 0
        produto = produtoSaci
        nota = notaPrd
        usuario = RegistryUserInfo.usuarioDefault
        localizacao = locProduto
      }
    }

    fun isSave(notaSaci: NotaSaci): Boolean {
      return where().produto.codigo.eq(notaSaci.prdno?.padStart(16, ' '))
        .produto.grade.eq(notaSaci.grade)
        .nota.numero.eq(notaSaci.numeroSerie())
        .nota.loja.equalTo(lojaDefault)
        .exists()
    }
  }

  fun printEtiqueta() = NotaPrint(this)

  fun recalculaSaldos() {
    produto?.recalculaSaldos(localizacao = localizacao)
  }

  override fun save() {
    super.save()
    if(codigoBarraCliente.isNullOrEmpty()) codigoBarraCliente = viewCodigoBarraCliente?.codbar ?: ""
    if(codigoBarraConferencia.isNullOrEmpty()) codigoBarraConferencia = viewCodigoBarraConferencia?.codbar ?: ""
    if(codigoBarraEntrega.isNullOrEmpty()) codigoBarraEntrega = viewCodigoBarraEntrega?.codbar ?: ""
    super.save()
  }

  override fun insert() {
    super.insert()
    if(codigoBarraConferencia.isNullOrEmpty()) codigoBarraConferencia = viewCodigoBarraConferencia?.codbar ?: ""
    if(codigoBarraEntrega.isNullOrEmpty()) codigoBarraEntrega = viewCodigoBarraEntrega?.codbar ?: ""
    super.save()
  }

  fun desfazerOperacao() {
    if(status == ENT_LOJA || status == ENTREGUE || status == CONFERIDA) {
      status = INCLUIDA
      save()
    }
  }
}

class NotaPrint(val item: ItemNota) {
  val notaSaci = item.nota
  val rota = notaSaci?.rota ?: ""
  val nota = notaSaci?.numero ?: ""
  val tipoObservacao = notaSaci?.observacao?.split(" ")?.get(0) ?: ""
  val isNotaSaci = when(notaSaci?.tipoNota) {
    TipoNota.OUTROS_E -> false
    TipoNota.OUTROS_S -> false
    null              -> false
    else              -> true
  }
  val tipoNota = if(isNotaSaci) notaSaci?.tipoNota?.descricao ?: ""
  else tipoObservacao
  val dataLocal = if(isNotaSaci) notaSaci?.data
  else item.data
  val data = dataLocal?.format()
  val produto = item.produto
  val sd = item.saldo ?: 0
  val quant = item.quantidade
  val prdno = produto?.codigo?.trim() ?: ""
  val grade = produto?.grade ?: ""
  val name = produto?.descricao ?: ""
  val prdnoGrade = "$prdno${if(grade == "") "" else "-$grade"}"
  val un
    get() = produto?.vproduto?.unidade ?: "UN"
  val loc = item.localizacao
  val codigoBarraEntrega
    get() = item.codigoBarraEntrega ?: ""
  val codigoBarraConferencia
    get() = item.codigoBarraConferencia ?: ""
  val codigoBarraCliente
    get() = item.codigoBarraCliente ?: ""
  val dataLancamento
    get() = item.nota?.lancamento?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
  val nomeFilial
    get() = "ENGECOPI ${item.nota?.loja?.sigla}"
  val numeroLoja = notaSaci?.loja?.numero ?: 0

  fun print(template: String): String {
    return NotaPrint::class.memberProperties.fold(template) {reduce, prop ->
      reduce.replace("[${prop.name}]", "${prop.get(this)}")
    }
  }
}

enum class StatusNota(val descricao: String, val tipoMov: TipoMov, val multiplicador: Int) {
  RECEBIDO("Recebido", ENTRADA, 1),
  INCLUIDA("Incluída", SAIDA, 0),
  CONFERIDA("Conferida", SAIDA, -1),
  ENTREGUE("Entregue", SAIDA, -1),
  ENT_LOJA("Entregue na Loja", SAIDA, 0)
}

