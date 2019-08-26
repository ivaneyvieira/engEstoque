package br.com.engecopi.estoque.model

import br.com.engecopi.estoque.model.RegistryUserInfo.abreviacaoDefault
import br.com.engecopi.estoque.model.RegistryUserInfo.usuarioDefault
import br.com.engecopi.estoque.model.StatusNota.INCLUIDA
import br.com.engecopi.estoque.model.TipoMov.ENTRADA
import br.com.engecopi.estoque.model.TipoMov.SAIDA
import br.com.engecopi.estoque.model.TipoNota.ACERTO_S
import br.com.engecopi.estoque.model.TipoNota.CANCELADA_E
import br.com.engecopi.estoque.model.TipoNota.CANCELADA_S
import br.com.engecopi.estoque.model.TipoNota.DEV_FOR
import br.com.engecopi.estoque.model.TipoNota.ENT_RET
import br.com.engecopi.estoque.model.TipoNota.OUTROS_S
import br.com.engecopi.estoque.model.TipoNota.PEDIDO_S
import br.com.engecopi.estoque.model.TipoNota.TRANSFERENCIA_S
import br.com.engecopi.estoque.model.TipoNota.VENDA
import br.com.engecopi.estoque.model.finder.NotaFinder
import br.com.engecopi.estoque.model.query.QNota
import br.com.engecopi.framework.model.BaseModel
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.saci.saci
import br.com.engecopi.utils.localDate
import io.ebean.annotation.Aggregation
import io.ebean.annotation.Cache
import io.ebean.annotation.CacheQueryTuning
import io.ebean.annotation.Index
import io.ebean.annotation.Length
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Size

@Entity
@Cache(enableQueryCache = true)
@CacheQueryTuning(maxSecsToLive = 30)
@Table(name = "notas")
@Index(columnNames = ["loja_id", "tipo_mov", "numero"], unique = true)
class Nota: BaseModel() {
  @Size(max = 40)
  @Index(unique = false)
  var numero: String = ""
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
  var lancamentoOrigem: LancamentoOrigem = LancamentoOrigem.EXPEDICAO
  val multipicadorCancelado
    get() = if(tipoNota == CANCELADA_E || tipoNota == CANCELADA_S) 0 else 1

  companion object Find: NotaFinder() {
    fun createNota(notasaci: NotaSaci?): Nota? {
      notasaci ?: return null
      val tn = TipoNota.value(notasaci.tipo) ?: return null
      val numero = notasaci.numeroSerie()

      return findNota(numero, tn.tipoMov) ?: Nota().apply {
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

    fun createNotaItens(notasaci: List<NotaSaci>): NotaItens {
      val notaSimples = notasaci.firstOrNull() ?: return NotaItens.VAZIO
      val numero = notaSimples.numeroSerie()
      val tipoNota = notaSimples.tipoNota() ?: return NotaItens.VAZIO
      val nota = findNota(numero, tipoNota.tipoMov) ?: createNota(notaSimples) ?: return NotaItens.VAZIO
      nota.sequencia = maxSequencia() + 1
      nota.usuario = usuarioDefault
      val itens = notasaci.mapNotNull {item ->
        val produto = Produto.findProduto(item.prdno, item.grade)
        ItemNota.find(nota, produto) ?: ItemNota.createItemNota(item, nota, abreviacaoDefault)?.let {itemNota ->
          itemNota.status = INCLUIDA
          itemNota.usuario = usuarioDefault
          itemNota
        }
      }
      return NotaItens(nota, itens)
    }

    fun maxSequencia(): Int {
      return where().select(QNota._alias.maxSequencia).findList().firstOrNull()?.maxSequencia ?: 0
    }

    fun findEntrada(numero: String?): Nota? {
      val loja = RegistryUserInfo.lojaDefault
      return if(numero.isNullOrBlank()) null
      else Nota.where().tipoMov.eq(ENTRADA).numero.eq(numero).loja.id.eq(loja.id).findList().firstOrNull()
    }

    fun findSaida(numero: String?): Nota? {
      val loja = RegistryUserInfo.lojaDefault
      return if(numero.isNullOrBlank()) null
      else Nota.where().tipoMov.eq(SAIDA).numero.eq(numero).loja.id.eq(loja.id).findList().firstOrNull()
    }

    fun findNota(numero: String?, tipoMov: TipoMov): Nota? {
      return when(tipoMov) {
        ENTRADA -> findEntrada(numero)
        SAIDA   -> findSaida(numero)
      }
    }

    fun novoNumero(): Int {
      val regex = "[0-9]+".toRegex()
      val max = where().findList().asSequence().map {it.numero}.filter {regex.matches(it)}.max() ?: "0"
      val numMax = max.toIntOrNull() ?: 0
      return numMax + 1
    }

    fun findNotaEntradaSaci(numeroNF: String?): List<NotaSaci> {
      numeroNF ?: return emptyList()
      val loja = RegistryUserInfo.lojaDefault
      val numero = numeroNF.split("/").getOrNull(0) ?: return emptyList()
      val serie = numeroNF.split("/").getOrNull(1) ?: ""
      return saci.findNotaEntrada(loja.numero, numero, serie, usuarioDefault.admin)
    }

    fun findNotaSaidaSaci(numeroNF: String?): List<NotaSaci> {
      numeroNF ?: return emptyList()
      val loja = RegistryUserInfo.lojaDefault
      val numero = numeroNF.split("/").getOrNull(0) ?: return emptyList()
      val serie = numeroNF.split("/").getOrNull(1) ?: ""
      return saci.findNotaSaida(loja.numero, numero, serie, usuarioDefault.admin)
    }

    fun itemDuplicado(nota: Nota?, produto: Produto?): Boolean {
      val lojaId = nota?.loja?.id ?: return false
      val numero = nota.numero
      val tipoMov = nota.tipoMov
      val produtoId = produto?.id ?: return false
      return ItemNota.where().nota.loja.id.eq(lojaId).nota.numero.eq(numero).nota.tipoMov.eq(tipoMov).produto.id.eq(
        produtoId).findCount() > 0
    }

    fun findNotaSaidaKey(nfeKey: String): List<NotaSaci> {
      return saci.findNotaSaidaKey(nfeKey, usuarioDefault.admin)
    }

    fun listSaidaCancel(): List<Nota> {
      val data = LocalDate.now().minusDays(10)
      val lista = Nota.where()
        .tipoMov.eq(SAIDA)
        .data.ge(data)
        .tipoNota.notEqualTo(PEDIDO_S)
        .findList()
      return saci.findNotasSaidaCancelada(lista)
    }
  }

  fun existe(): Boolean {
    return where().loja.equalTo(loja).numero.eq(numero).findCount() > 0
  }

  fun itensNota(): List<ItemNota> {
    return ItemNota.where()
      .nota.equalTo(this)
      .findList()
  }
}

enum class TipoMov(val descricao: String) {
  ENTRADA("Entrada"),
  SAIDA("Saida")
}

enum class TipoNota(val tipoMov: TipoMov, val descricao: String, val descricao2: String, val isFree: Boolean = false) {
  //Entrada
  COMPRA(ENTRADA, "Compra", "Compra"),
  TRANSFERENCIA_E(ENTRADA, "Transferencia", "Transferencia Entrada"),
  DEV_CLI(ENTRADA, "Dev Cliente", "Dev Cliente"),
  ACERTO_E(ENTRADA, "Acerto", "Acerto Entrada"),
  PEDIDO_E(ENTRADA, "Pedido", "Pedido Entrada"),
  OUTROS_E(ENTRADA, "Outros", "Outras Entradas", true),
  NOTA_E(ENTRADA, "Entradas", "Entradas", true),
  RECLASSIFICACAO_E(ENTRADA, "Reclassificação", "Reclassificação Entrada"),
  VENDA(SAIDA, "Venda", "Venda"),
  TRANSFERENCIA_S(SAIDA, "Transferencia", "Transferencia Saida"),
  ENT_RET(SAIDA, "Ent/Ret", "Ent/Ret"),
  DEV_FOR(SAIDA, "Dev Fornecedor", "Dev Fornecedor"),
  ACERTO_S(SAIDA, "Acerto", "Acerto Saida"),
  PEDIDO_S(SAIDA, "Pedido", "Pedido Saida"),
  OUTROS_S(SAIDA, "Outros", "Outras Saidas", true),
  OUTRAS_NFS(SAIDA, "Outras NFS", "Outras NF Saida", true),
  SP_REME(SAIDA, "Simples Remessa", "Simples Remessa", true),
  CANCELADA_E(ENTRADA, "Nota Cancelada", "NF Entrada Cancelada"),
  CANCELADA_S(SAIDA, "Nota Cancelada", "NF Saída Cancelada");

  companion object {
    fun valuesEntrada(): List<TipoNota> = values().filter {it.tipoMov == ENTRADA}

    fun valuesSaida(): List<TipoNota> = values().filter {it.tipoMov == SAIDA}

    fun value(valueStr: String?) = valueStr?.let {v ->
      values().find {it.toString() == v}
    }
  }
}

data class NotaSerie(val id: Long, val tipoNota: TipoNota) {
  val descricao = tipoNota.descricao

  companion object {
    fun findByTipo(tipo: TipoNota?): NotaSerie? {
      tipo ?: return null
      return values.find {it.tipoNota == tipo}
    }

    val values = listOf(NotaSerie(1, VENDA),
                        NotaSerie(2, ENT_RET),
                        NotaSerie(3, TRANSFERENCIA_S),
                        NotaSerie(4, ACERTO_S),
                        NotaSerie(5, PEDIDO_S),
                        NotaSerie(6, DEV_FOR),
                        NotaSerie(7, OUTROS_S))
  }
}

data class NotaItens(val nota: Nota?, val itens: List<ItemNota>) {
  val vazio get() = nota == null || itens.isEmpty()

  companion object {
    val VAZIO = NotaItens(null, emptyList())
  }
}

enum class LancamentoOrigem(val descricao: String) {
  EXPEDICAO("Expedição"),
  DEPOSITO("Deposito")
}