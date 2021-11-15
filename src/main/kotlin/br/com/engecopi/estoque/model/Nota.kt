package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.LancamentoOrigem.EXPEDICAO
import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.*
import br.com.engecopi.estoque.model.dtos.EntregaFutura
import br.com.engecopi.estoque.model.dtos.PedidoNotaRessuprimento
import br.com.engecopi.estoque.model.dtos.TransferenciaAutomatica
import br.com.engecopi.estoque.model.dtos.data
import br.com.engecopi.estoque.model.envelopes.Printer
import br.com.engecopi.estoque.model.finder.NotaFinder
import br.com.engecopi.estoque.model.query.QItemNota
import br.com.engecopi.estoque.model.query.QNota
import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.saci.beans.NotaProdutoSaci
import br.com.engecopi.saci.saci
import br.com.engecopi.utils.localDate
import io.ebean.annotation.*
import io.ebean.annotation.Cache
import io.ebean.annotation.Index
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*
import javax.persistence.CascadeType.*
import javax.validation.constraints.Size

@Entity
@Indices(Index(columnNames = ["tipo_mov", "tipo_nota", "sequencia"]),
         Index(columnNames = ["loja_id", "tipo_mov", "numero"], unique = true))
@Cache(enableQueryCache = true)
@CacheQueryTuning(maxSecsToLive = 30)
@Table(name = "notas")
class Nota : BaseModel() {
  @Size(max = 40)
  @Index(unique = false)
  var numero: String = ""

  @Size(max = 40)
  @Index(unique = false)
  var numeroEntrega: String = ""

  @Enumerated(EnumType.STRING)
  var tipoMov: TipoMov = ENTRADA

  @Enumerated(EnumType.STRING)
  var tipoNota: TipoNota? = null

  @Size(max = 6)
  var rota: String = ""

  @Length(60)
  var fornecedor: String = ""

  @Length(60)
  var cliente: String = ""
  var lancamento: LocalDate = LocalDate.now()
  var data: LocalDate = LocalDate.now()
  var dataEmissao: LocalDate = LocalDate.now()
  var hora: LocalTime = LocalTime.now()

  @Size(max = 100)
  var observacao: String = ""

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var loja: Loja? = null

  @OneToMany(mappedBy = "nota", cascade = [PERSIST, MERGE, REFRESH])
  val itensNota: List<ItemNota>? = null

  @Column(name = "sequencia", columnDefinition = "int(11) default 0")
  var sequencia: Int = 0

  @ManyToOne(cascade = [PERSIST, MERGE, REFRESH])
  var usuario: Usuario? = null

  @Aggregation("max(sequencia)")
  var maxSequencia: Int? = 0

  @Enumerated(EnumType.STRING)
  var lancamentoOrigem: LancamentoOrigem = EXPEDICAO
  val cancelado
    get() = tipoNota == CANCELADA_E || tipoNota == CANCELADA_S
  val multipicadorCancelado
    get() = if (cancelado) 0 else 1
  val nfno
    get() = numero.split("/").getOrNull(0) ?: ""
  val nfse
    get() = numero.split("/").getOrNull(1) ?: ""

  fun updateFromSaci() {
    val storeno = loja?.numero ?: 0
    val notaInfo = when (tipoMov) {
                     ENTRADA -> saci.findNotaEntradaInfo(storeno, nfno, nfse)
                     SAIDA   -> saci.findNotaSaidaInfo(storeno, nfno, nfse)
                   } ?: return
    tipoNota = if (notaInfo.cancelado) when (tipoMov) {
      ENTRADA -> CANCELADA_E
      SAIDA   -> CANCELADA_S
    }
    else {
      notaInfo.tipoNota
    }
    this.save()
  }

  companion object Find : NotaFinder() {
    fun createNota(notasaci: NotaProdutoSaci?): Nota? {
      notasaci ?: return null
      val tn = TipoNota.value(notasaci.tipo) ?: return null
      val numero = notasaci.numeroSerie()
      val loja = notasaci.loja() ?: return null

      return findNota(loja, numero, tn.tipoMov) ?: Nota().apply {
        this.numero = notasaci.numeroSerie()
        this.tipoNota = tn
        this.tipoMov = tn.tipoMov
        this.rota = notasaci.rota ?: ""
        this.fornecedor = notasaci.vendName ?: ""
        this.cliente = notasaci.clienteName ?: ""
        this.data = notasaci.date?.localDate() ?: LocalDate.now()
        this.dataEmissao = notasaci.dtEmissao?.localDate() ?: LocalDate.now()
        this.loja = Loja.findLoja(notasaci.storeno)
      }
    }

    fun createNotaItens(notasaci: List<NotaProdutoSaci>): NotaItens {
      val notaSimples = notasaci.firstOrNull() ?: return NotaItens.erro("Nota não encontrada")
      val numero = notaSimples.numeroSerie()
      val tipoNota = notaSimples.tipoNota() ?: return NotaItens.erro("Nota com tipo inválido")
      val loja = notaSimples.loja() ?: return NotaItens.erro("Nota com loja inválido")
      val nota =
              findNota(loja, numero, tipoNota.tipoMov) ?: createNota(notaSimples)
              ?: return NotaItens.erro("Erro ao criar a nota")
      nota.sequencia = maxSequencia() + 1
      nota.usuario = usuarioDefault
      val itens = notasaci.mapNotNull { item ->
        val produto = Produto.findProduto(item.prdno, item.grade)
        ItemNota.find(nota, produto) ?: ItemNota.createItemNota(item, nota, abreviacaoDefault)?.let { itemNota ->
            itemNota.status = INCLUIDA
            itemNota.usuario = usuarioDefault
            itemNota
          }
      }
      return NotaItens(nota, itens)
    }

    fun maxSequencia(): Int {
      return QNota().select(QNota._alias.maxSequencia).findList().firstOrNull()?.maxSequencia ?: 0
    }

    fun findEntrada(loja: Loja, numero: String?): Nota? {
      return if (numero.isNullOrBlank()) null
      else QNota().tipoMov.eq(ENTRADA).numero.eq(numero).loja.id.eq(loja.id).findList().firstOrNull()
    }

    fun findSaida(storeno: Int?, numero: String?): Nota? {
      storeno ?: return null
      return if (numero.isNullOrBlank()) null
      else QNota().tipoMov.eq(SAIDA).numero.eq(numero).loja.numero.eq(storeno).findList().firstOrNull()
    }

    fun findSaida(loja: Loja?, numero: String?): Nota? {
      val storeno = loja?.numero ?: return null
      return findSaida(storeno, numero)
    }

    fun findNota(loja: Loja?, numero: String?, tipoMov: TipoMov): Nota? {
      loja ?: return null
      return when (tipoMov) {
        ENTRADA -> findEntrada(loja, numero)
        SAIDA   -> findSaida(loja, numero)
      }
    }

    fun novoNumero(): Int {
      val regex = "[0-9]+".toRegex()
      val max = QNota().findList().asSequence().map { it.numero }.filter { regex.matches(it) }.maxOrNull() ?: "0"
      val numMax = max.toIntOrNull() ?: 0
      return numMax + 1
    }

    fun findNotaEntradaSaci(loja: Loja, numeroNF: String?): List<NotaProdutoSaci> {
      numeroNF ?: return emptyList()
      val numero = numeroNF.split("/").getOrNull(0) ?: return emptyList()
      val serie = numeroNF.split("/").getOrNull(1) ?: ""
      return saci.findNotaEntrada(loja.numero, numero, serie, usuarioDefault.admin)
    }

    fun findNotaSaidaSaci(loja: Loja, numeroNF: String?): List<NotaProdutoSaci> {
      return findNotaSaidaSaci(loja.numero, numeroNF)
    }

    fun findNotaSaidaSaci(storeno: Int?, numeroNF: String?): List<NotaProdutoSaci> {
      numeroNF ?: return emptyList()
      storeno ?: return emptyList()
      val numero = numeroNF.split("/").getOrNull(0) ?: return emptyList()
      val serie = numeroNF.split("/").getOrNull(1) ?: ""
      return saci.findNotaSaida(storeno, numero, serie, usuarioDefault.admin)
    }

    fun itemDuplicado(nota: Nota?, produto: Produto?): Boolean {
      val lojaId = nota?.loja?.id ?: return false
      val numero = nota.numero
      val tipoMov = nota.tipoMov
      val produtoId = produto?.id ?: return false
      return QItemNota().nota.loja.id.eq(lojaId).nota.numero.eq(numero).nota.tipoMov.eq(tipoMov).produto.id.eq(produtoId)
               .findCount() > 0
    }

    fun findNotaSaidaKey(nfeKey: String): List<NotaProdutoSaci> {
      return saci.findNotaSaidaKey(nfeKey, usuarioDefault.admin)
    }

    fun listSaidaCancel(): List<Nota> {
      val data = LocalDate.now().minusDays(10)
      val lista = QNota().tipoMov.eq(SAIDA).data.ge(data).tipoNota.notEqualTo(PEDIDO_S).findList()
      return saci.findNotasSaidaCancelada(lista)
    }

    fun notasSaidaSalva(loja: Loja): List<Nota> {
      return notasSalva(loja, SAIDA)
    }

    fun notasEntradaSalva(loja: Loja): List<Nota> {
      return notasSalva(loja, ENTRADA)
    }

    private fun notasSalva(loja: Loja, tipoNota: TipoMov): List<Nota> {
      val dtInicial = LocalDate.of(2020, 1, 1)
      return QNota().tipoMov.eq(tipoNota).loja.equalTo(loja).itensNota.localizacao.startsWith(abreviacaoDefault).data.after(
          dtInicial).findList()
    }

    fun notaBaixa(storeno: Int?, numero: String?) = TransferenciaAutomatica.notaBaixa(storeno, numero)
      .ifEmpty { EntregaFutura.notaBaixa(storeno, numero) }
      .ifEmpty { PedidoNotaRessuprimento.notaBaixa(numero) }

    fun notaFatura(storeno: Int?, numero: String?) = TransferenciaAutomatica.notaFatura(storeno, numero)
      .ifEmpty { EntregaFutura.notaFatura(storeno, numero) }
      .ifEmpty { PedidoNotaRessuprimento.pedidoRessuprimento(storeno, numero) }
  }

  fun dataBaixa(): LocalDate? = notaBaixa().data

  fun notaBaixa() = notaBaixa(loja?.numero, numero)

  fun notaFatura() = notaFatura(loja?.numero, numero)

  fun existe(): Boolean {
    return QNota().loja.equalTo(loja).tipoMov.eq(tipoMov).numero.eq(numero).findCount() > 0
  }

  fun itensNota(): List<ItemNota> {
    return QItemNota().nota.equalTo(this).findList()
  }
}

enum class TipoMov(val descricao: String) {
  ENTRADA("Entrada"), SAIDA("Saida")
}

enum class TipoNota(val tipoMov: TipoMov,
                    val descricao: String,
                    val descricao2: String,
                    val lojaDeposito: Boolean = true) {
  //Entrada
  COMPRA(ENTRADA, "Compra", "Compra"),
  TRANSFERENCIA_E(ENTRADA, "Transferencia", "Transferencia Entrada"),
  DEV_CLI(ENTRADA, "Dev Cliente", "Dev Cliente"),
  ACERTO_E(ENTRADA, "Acerto", "Acerto Entrada"),
  PEDIDO_E(ENTRADA, "Pedido", "Pedido Entrada"),
  OUTROS_E(ENTRADA, "Outros", "Outras Entradas"),
  NOTA_E(ENTRADA, "Entradas", "Entradas"),
  RECLASSIFICACAO_E(ENTRADA, "Reclassificação", "Reclassificação Entrada"),
  VENDAF(SAIDA, "Venda Futura", "Venda Fut", false),
  RETIRAF(SAIDA, "Retira Futura", "Retira Fut", false),
  VENDA(SAIDA, "Venda", "Venda"),
  TRANSFERENCIA_S(SAIDA, "Transferencia", "Transferencia Saida"),
  ENT_RET(SAIDA, "Ent/Ret", "Ent/Ret"),
  DEV_FOR(SAIDA, "Dev Fornecedor", "Dev Fornecedor"),
  ACERTO_S(SAIDA, "Acerto", "Acerto Saida"),
  PEDIDO_S(SAIDA, "Pedido", "Pedido Saida"),
  PEDIDO_A(SAIDA, "Abastecimento", "Pedido Abastecimento"),
  PEDIDO_R(SAIDA, "Ressuprimento", "Pedido de Ressuprimento", false),
  OUTROS_S(SAIDA, "Outros", "Outras Saidas"),
  CHAVE_SAIDA(SAIDA, "Chave de Nota", "Chave de Nota"),
  OUTRAS_NFS(SAIDA, "Outras NFS", "Outras NF Saida"),
  SP_REME(SAIDA, "Simples Remessa", "Simples Remessa"),
  CANCELADA_E(ENTRADA, "Nota Cancelada", "NF Entrada Cancelada"),
  CANCELADA_S(SAIDA, "Nota Cancelada", "NF Saída Cancelada");

  companion object {
    fun valuesEntrada(): List<TipoNota> = values().filter { it.tipoMov == ENTRADA }

    fun valuesSaida(): List<TipoNota> = values().filter { it.tipoMov == SAIDA }

    fun value(valueStr: String?) = valueStr?.let { v ->
      values().find { it.toString() == v }
    }

    val lojasExternas
      get() = values().filter { !it.lojaDeposito }
  }
}

data class NotaSerie(val id: Long, val tipoNota: TipoNota) {
  val descricao = tipoNota.descricao

  companion object {
    fun findByTipo(tipo: TipoNota?): NotaSerie? {
      tipo ?: return null
      return values.find { it.tipoNota == tipo }
    }

    val values =
            listOf(NotaSerie(1, VENDA),
                   NotaSerie(2, ENT_RET),
                   NotaSerie(3, TRANSFERENCIA_S),
                   NotaSerie(4, ACERTO_S),
                   NotaSerie(5, PEDIDO_S),
                   NotaSerie(6, DEV_FOR),
                   NotaSerie(7, VENDAF),
                   NotaSerie(8, OUTRAS_NFS),
                   NotaSerie(9, CHAVE_SAIDA))
  }
}

data class NotaItens(val nota: Nota?, val itens: List<ItemNota>, val msgErro: String = "") {
  val vazio get() = nota == null || itens.isEmpty()

  companion object {
    fun erro(msgErro: String) = NotaItens(null, emptyList(), msgErro)
  }
}

enum class LancamentoOrigem(val descricao: String) {
  EXPEDICAO("Expedição") {
    override fun printer(): Printer = Printer(usuarioDefault.impressoraExpedicao())
  },
  DEPOSITO("Deposito") {
    override fun printer(): Printer = Printer("ENTREGA")
  },
  ENTREGA_F("Entrega Futura") {
    override fun printer(): Printer = Printer("")
  },
  RESSUPRI("Ressuprimento") {
    override fun printer(): Printer = Printer("")
  },
  ABASTECI("Abastecimento") {
    override fun printer(): Printer = Printer(usuarioDefault.impressoraExpedicao())
  };

  abstract fun printer(): Printer
}