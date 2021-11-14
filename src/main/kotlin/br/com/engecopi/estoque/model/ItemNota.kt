package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.*
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.dtos.filterProduto
import br.com.engecopi.estoque.model.finder.ItemNotaFinder
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.utils.format
import br.com.engecopi.utils.formatMesAno
import io.ebean.annotation.*
import io.ebean.annotation.Cache
import io.ebean.annotation.Index
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.persistence.*
import javax.persistence.CascadeType.*
import javax.validation.constraints.Size
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties

@Entity
@Cache(enableQueryCache = true)
@CacheQueryTuning(maxSecsToLive = 30)
@Table(name = "itens_nota")
@Indices(Index(unique = true, columnNames = ["nota_id", "produto_id", "localizacao"]),
         Index(columnNames = ["produto_id", "nota_id"]))
class ItemNota : BaseModel() {
  var data: LocalDate = LocalDate.now()
  var hora: LocalTime = LocalTime.now()
  var quantidade: Int = 0
  var quantidadeSaci: Int? = null

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var produto: Produto? = null

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var nota: Nota? = null

  var dataFabricacao: LocalDate? = null

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var etiqueta: Etiqueta? = null

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var usuario: Usuario? = null
  var saldo: Int? = 0
  var impresso: Boolean = false

  @Length(60)
  var localizacao: String = ""

  @Enumerated(EnumType.STRING)
  @Index
  var status: StatusNota = ENTREGUE

  @Size(max = 60)
  var codigoBarraCliente: String? = ""

  @Size(max = 60)
  var codigoBarraConferencia: String? = ""

  @Size(max = 60)
  var codigoBarraConferenciaBaixa: String? = ""

  @Size(max = 60)
  var codigoBarraEntrega: String? = ""
  val quantidadeSaldo: Int
    get() = (status.multiplicador) * quantidade * (nota?.multipicadorCancelado ?: 0)
  val viewCodigoBarraConferencia: ViewCodBarConferencia?
    get() = ViewCodBarConferencia.byId(id)
  val viewCodigoBarraCliente: ViewCodBarCliente?
    get() = ViewCodBarCliente.byId(nota?.id)
  val viewCodigoBarraEntrega: ViewCodBarEntrega?
    get() = ViewCodBarEntrega.byId(id)
  val descricao: String?
    get() = produto?.descricao
  val codigo: String?
    get() = produto?.codigo
  val grade: String?
    get() = produto?.grade
  val numeroNota: String?
    get() = nota?.numero
  val rota: String?
    get() = nota?.rota
  val tipoMov: TipoMov?
    get() = nota?.tipoMov
  val tipoNota: TipoNota?
    get() = nota?.tipoNota
  val dataNota: LocalDate?
    get() = nota?.data
  val quantidadeVolume
    get() = ceil((quantidade * 1.00).div(produto?.quantidadePacote ?: 1)).roundToInt()
  val ultilmaMovimentacao: Boolean
    get() {
      return produto?.ultimaNota()?.let {
        it.id == this.id
      } ?: true
    }
  private val abrev: String
    get() = localizacao.split('.').getOrNull(0) ?: ""
  val loja: Loja?
    get() = nota?.loja
  val abreviacao: Abreviacao?
    get() = Abreviacao.findByAbreviacao(abrev)
  val numeroEntrega
    get() = nota?.notaBaixa()?.filterProduto(codigo, grade) ?: emptyList()
  val dataEntrega
    get() = nota?.dataBaixa()

  companion object Find : ItemNotaFinder() {
    fun find(loja: Int?, numero: String?): List<ItemNota> {
      loja ?: return emptyList()
      numero ?: return emptyList()
      return QItemNota().nota.loja.numero.eq(loja).nota.numero.eq(numero).findList()
    }

    fun find(nota: Nota?, produto: Produto?): ItemNota? { //TODO Depois pensar na possibilidade de mais de um
      nota ?: return null
      produto ?: return null
      return QItemNota().nota.fetchQuery().nota.id.eq(nota.id).produto.id.eq(produto.id).findList().firstOrNull()
    }

    fun find(notaProdutoSaci: NotaProdutoSaci?): ItemNota? {
      notaProdutoSaci ?: return null
      val storeno = notaProdutoSaci.storeno ?: return null
      val produto = Produto.findProduto(notaProdutoSaci.prdno, notaProdutoSaci.grade) ?: return null
      return QItemNota().nota.fetchQuery().nota.numero.eq("${notaProdutoSaci.numero}/${notaProdutoSaci.serie}").nota.loja.numero.eq(
        storeno).produto.equalTo(produto).findList().firstOrNull()
    }

    fun createItemNota(notaProdutoSaci: NotaProdutoSaci, notaPrd: Nota?, abreviacao: String?): ItemNota? {
      notaPrd ?: return null
      val produtoSaci = Produto.findProduto(notaProdutoSaci.prdno, notaProdutoSaci.grade) ?: return null
      val locProduto = ViewProdutoLoc.localizacoesProduto(produtoSaci).firstOrNull {
        it.startsWith(abreviacao ?: "")
      } ?: ""
      val item = find(notaPrd, produtoSaci)

      return item ?: ItemNota().apply {
        quantidade = notaProdutoSaci.quant ?: 0
        quantidadeSaci = quantidade
        produto = produtoSaci
        nota = notaPrd
        usuario = usuarioDefault
        localizacao = locProduto
      }
    }

    fun isSave(notaProdutoSaci: NotaProdutoSaci): Boolean {
      val numeroSerie = notaProdutoSaci.numeroSerie()
      val tipoMov = notaProdutoSaci.tipoNota()?.tipoMov ?: return false
      val loja = Loja.findLoja(notaProdutoSaci.storeno) ?: return false
      val nota = Nota.findNota(loja, numeroSerie, tipoMov) ?: return false
      val produto = Produto.findProduto(notaProdutoSaci.prdno, notaProdutoSaci.grade) ?: return false
      return QItemNota().produto.eq(produto).nota.eq(nota).exists()
    }

    fun findItensBarcodeCliente(barcode: String): List<ItemNota> {
      return QItemNota().codigoBarraCliente.eq(barcode).findList()
    }
  }

  fun printEtiqueta(volume: Int? = null) = NotaPrint(this, volume)

  fun recalculaSaldos() {
    produto?.recalculaSaldos(localizacao = localizacao)
    this.refresh()
  }

  override fun save() {
    super.save()
    if (codigoBarraCliente.isNullOrEmpty()) {
      codigoBarraCliente = viewCodigoBarraCliente?.codbar ?: ""
    }
    if (codigoBarraConferencia.isNullOrEmpty()) {
      codigoBarraConferencia = viewCodigoBarraConferencia?.codbar ?: ""
      codigoBarraConferenciaBaixa = if (codigoBarraConferencia != "") {
        codigoBarraConferencia + " ENTREGUE"
      }
      else ""
    }
    if (codigoBarraEntrega.isNullOrEmpty()) {
      codigoBarraEntrega = viewCodigoBarraEntrega?.codbar ?: ""
    }
    super.save()
  }

  override fun insert() {
    super.insert()
    if (codigoBarraConferencia.isNullOrEmpty()) {
      codigoBarraConferencia = viewCodigoBarraConferencia?.codbar ?: ""
      codigoBarraConferenciaBaixa = if (codigoBarraConferencia != "") {
        codigoBarraConferencia + "BAIXA"
      }
      else ""
    }
    if (codigoBarraEntrega.isNullOrEmpty()) {
      codigoBarraEntrega = viewCodigoBarraEntrega?.codbar ?: ""
    }

    super.save()
  }

  fun desfazerOperacao() {
    if (status == ENT_LOJA || status == ENTREGUE || status == CONFERIDA) {
      status = INCLUIDA
      save()
    }
  }
}

class NotaPrint(val item: ItemNota, val volume: Int? = null) {
  val notaSaci = item.nota
  val rota = notaSaci?.rota ?: ""
  val nota = notaSaci?.numero ?: ""
  val tipoObservacao = notaSaci?.observacao?.split(" ")?.get(0) ?: ""
  val isNotaSaci = when (notaSaci?.tipoNota) {
    TipoNota.OUTROS_E -> false
    TipoNota.OUTROS_S -> false
    null              -> false
    else              -> true
  }
  val tipoNota = if (isNotaSaci) notaSaci?.tipoNota?.descricao ?: ""
  else tipoObservacao
  val dataLocal = if (isNotaSaci) notaSaci?.data
  else item.data
  val data = dataLocal?.format()
  val produto = item.produto
  val sd = item.saldo ?: 0
  val quant = item.quantidade
  val prdno = produto?.codigo?.trim() ?: ""
  val grade = produto?.grade ?: ""
  val name = produto?.descricao ?: ""
  val prdnoGrade = "$prdno${if (grade == "") "" else "-$grade"}"
  val codebar
    get() = produto?.barcodeGtin?.maxOrNull() ?: ""
  val un
    get() = produto?.vproduto?.unidade ?: "UN"
  val loc = item.localizacao
  val codigoBarraEntrega
    get() = item.codigoBarraEntrega ?: ""
  val codigoBarraConferencia
    get() = item.codigoBarraConferencia ?: ""
  val codigoBarraConferenciaBaixa
    get() = item.codigoBarraConferenciaBaixa ?: ""
  val codigoBarraCliente
    get() = item.codigoBarraCliente ?: ""
  val dataLancamento
    get() = item.nota?.lancamento?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: ""
  val fabricacao
    get() = item.dataFabricacao?.formatMesAno() ?: ""
  val numFab
    get() = item.dataFabricacao?.format(DateTimeFormatter.ofPattern("yyyyMM"))?.toIntOrNull() ?: 0
  val quantidadePacote
    get() = produto?.quantidadePacote ?: 1
  val quantidadeVolume: Int
    get() {
      volume ?: return quant
      val saldo = quant - (volume - 1) * quantidadePacote
      return if (saldo > quantidadePacote) quantidadePacote else saldo
    }
  val codigoBarraVolume
    get() = "$codebar $numFab $quantidadeVolume $volume"
  val nomeFilial
    get() = "ENGECOPI ${item.nota?.loja?.sigla}"
  val numeroLoja = notaSaci?.loja?.numero ?: 0
  val horaLancamento = notaSaci?.hora?.format() ?: ""

  fun print(template: String): String {
    return NotaPrint::class.memberProperties.fold(template) { reduce, prop ->
      reduce.replace("[${prop.name}]", "${prop.get(this)}")
    }
  }
}

enum class StatusNota(val descricao: String, val tipoMov: TipoMov, val multiplicador: Int) {
  RECEBIDO("Recebido", ENTRADA, 1),
  INCLUIDA("Incluída", SAIDA, 0),
  CONFERIDA("Conferida", SAIDA, -1),
  ENTREGUE("Entregue", SAIDA, -1),
  ENT_LOJA("Entregue na Loja", SAIDA, 0),
  PRODUTO("Etiqueta Produto", SAIDA, 0)
}

