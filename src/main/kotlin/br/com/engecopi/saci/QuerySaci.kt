package br.com.engecopi.saci

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.estoque.model.dtos.*
import br.com.engecopi.saci.beans.*
import br.com.engecopi.utils.DB
import br.com.engecopi.utils.localDate
import br.com.engecopi.utils.lpad
import br.com.engecopi.utils.toSaciDate
import java.time.LocalDate

class QuerySaci : QueryDB(driver, url, username, password) {
  fun findNotaEntrada(storeno: Int, nfname: String, invse: String, liberaNotasAntigas: Boolean): List<NotaProdutoSaci> {
    val sql = "/sqlSaci/findNotaEntrada.sql"
    return if (nfname == "") emptyList()
    else query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfname", nfname)
        .addParameter("invse", invse)
        .executeAndFetch(NotaProdutoSaci::class.java)
    }.filter { liberaNotasAntigas || filtroDataRecente(it) }
  }

  fun findNotaSaida(storeno: Int, nfno: String, nfse: String, liberaNotasAntigas: Boolean): List<NotaProdutoSaci> {
    return when (nfno) {
      ""   -> emptyList()
      else -> when (nfse) {
        ""   -> when (nfno.length) {
          9    -> findPedidoRessuprimento(nfno.toIntOrNull())
          else -> findNotaSaidaOrd(storeno, nfno)
        }
        else -> {
          val nfs = findNotaSaidaNF(storeno, nfno, nfse)
          when {
            nfs.isNotEmpty() -> nfs
            else             -> findNotaSaidaPxa(storeno, nfno, nfse)
          }
        }
      }.filter { liberaNotasAntigas || filtroDataRecente(it) }
    }
  }

  fun findPedidoRessuprimento(ordno: Int?): List<NotaProdutoSaci> {
    ordno ?: return emptyList()
    val sql = "/sqlSaci/pedidosRessuprimento.sql"
    return query(sql) { q ->
      q.addParameter("ordno", ordno).executeAndFetch(NotaProdutoSaci::class.java)
    }
  }

  fun findNotaSaidaInfo(storeno: Int, nfno: String, nfse: String): NotaSaciInfo? {
    val sql = "/sqlSaci/findNotaSaidaInfo.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
      q.addParameter("nfno", nfno.toIntOrNull())
      q.addParameter("nfse", nfse)
      q.executeAndFetch(NotaSaciInfo::class.java).firstOrNull()
    }
  }

  fun findNotaEntradaInfo(storeno: Int, nfno: String, nfse: String): NotaSaciInfo? {
    val sql = "/sqlSaci/findNotaEntradaInfo.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
      q.addParameter("nfno", nfno)
      q.addParameter("nfse", nfse)
      q.executeAndFetch(NotaSaciInfo::class.java).firstOrNull()
    }
  }

  private fun filtroDataRecente(notaProdutoSaci: NotaProdutoSaci): Boolean {
    val dtEmissao = notaProdutoSaci.dtEmissao?.localDate() ?: return false
    val date = notaProdutoSaci.date?.localDate() ?: return false
    val dataLimite = LocalDate.now().minusDays(150)
    return date.isAfter(dataLimite) || dtEmissao.isAfter(dataLimite)
  }

  private fun findNotaSaidaNF(storeno: Int, nfno: String, nfse: String): List<NotaProdutoSaci> {
    val sql = "/sqlSaci/findNotaSaidaNF.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfno", nfno.toIntOrNull())
        .addParameter("nfse", nfse)
        .executeAndFetch(NotaProdutoSaci::class.java)
    }
  }

  private fun findNotaSaidaOrd(storeno: Int, nfno: String): List<NotaProdutoSaci> {
    val sql = "/sqlSaci/findNotaSaidaOrd.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfno", nfno.toIntOrNull())
        .executeAndFetch(NotaProdutoSaci::class.java)
    }
  }

  private fun findNotaSaidaPxa(storeno: Int, nfno: String, nfse: String): List<NotaProdutoSaci> {
    val sql = "/sqlSaci/findNotaSaidaPXA.sql"
    val notas = query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfno", nfno.toIntOrNull())
        .addParameter("nfse", nfse)
        .executeAndFetch(NotaProdutoSaci::class.java)
    }
    return notas
  }

  fun findLojas(storeno: Int): List<LojaSaci> {
    val sql = "/sqlSaci/findLojas.sql"
    return query(sql) { q ->
      q.executeAndFetch(LojaSaci::class.java).filter { it.storeno == storeno || storeno == 0 }
    }
  }

  fun findUser(login: String): UserSaci? {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql) { q ->
      q.addParameter("login", login).executeAndFetch(UserSaci::class.java).firstOrNull()
    }
  }

  fun findLoginUser(): List<String> {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql) { q ->
      q.addParameter("login", "TODOS").executeAndFetch(UserSaci::class.java).mapNotNull { it.login }
    }
  }

  fun findNotaSaidaKey(nfeKey: String, liberaNotasAntigas: Boolean): List<NotaProdutoSaci> {
    val sql = "/sqlSaci/findNotaSaidaKey.sql"
    return query(sql) { q ->
      q.addParameter("nfekey", nfeKey).executeAndFetch(NfsKey::class.java).firstOrNull()
    }?.let { key ->
      findNotaSaida(key.storeno, key.nfno, key.nfse, liberaNotasAntigas)
    } ?: emptyList()
  }

  fun findBarcode(storeno: Int, barcode: String): List<ChaveProduto> {
    val sql = "/sqlSaci/findBarcode.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("barcode", barcode.lpad(16, " "))
        .executeAndFetch(ChaveProduto::class.java)
        .findChave()
    }
  }

  fun findBarcode(storeno: Int, prdno: String, grade: String): List<ChaveProduto> {
    val sql = "/sqlSaci/findBarcode2.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("prdno", prdno)
        .addParameter("grade", grade)
        .executeAndFetch(ChaveProduto::class.java)
        .findChave()
    }
  }

  fun findFornecedor(vendno: Int): List<ChaveProduto> {
    val sql = "/sqlSaci/findFornecedor.sql"
    return query(sql) { q ->
      q.addParameter("vendno", vendno).executeAndFetch(ChaveProduto::class.java)
    }
  }

  fun findCentroLucro(clno: Int): List<ChaveProduto> {
    val sql = "/sqlSaci/findCentroLucro.sql"
    return query(sql) { q ->
      q.addParameter("clno", clno).executeAndFetch(ChaveProduto::class.java)
    }
  }

  fun findTipoProduto(typeno: Int): List<ChaveProduto> {
    val sql = "/sqlSaci/findTipoProduto.sql"
    return query(sql) { q ->
      q.addParameter("typeno", typeno).executeAndFetch(ChaveProduto::class.java)
    }
  }

  fun findNotasSaidaCancelada(lista: List<Nota>): List<Nota> {
    val sqlTemp = temporaryTable("TempNotaSaida", lista) { nota ->
      val storeno = nota.loja?.numero ?: 0
      val nfno = nota.numero.split("/").getOrNull(0) ?: ""
      val nfse = nota.numero.split("/").getOrNull(1) ?: ""
      "$storeno AS storeno, '$nfno' AS nfno, '$nfse' AS nfse"
    }
    println(sqlTemp)
    return lista
  }

  fun findNotaEntradaSaci(storeno: Int, abreviacao: String?): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaEntradaTodas.sql"
    return findNotaSaci(sql, storeno, abreviacao)
  }

  fun findNotaSaidaSaci(storeno: Int, abreviacao: String?): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaSaidaTodas.sql"
    return findNotaSaci(sql, storeno, abreviacao)
  }

  private fun findNotaSaci(sql: String, storeno: Int, abreviacao: String?): List<NotaSaci> {
    val data = LocalDate.of(2020, 1, 1).toSaciDate()
    val notaProdutoList = query(sql) { q ->
      q.run {
        addParameter("storeno", storeno).addParameter("abreviacao", "${abreviacao ?: ""}%")
          .addParameter("data", data)
          .executeAndFetch(NotaProduto::class.java)
      }
    }.toList()
    val notaGroup = notaProdutoList.groupBy { KeyNota(it.storeno, it.numero, it.serie) }
    ProdutoSaci.updateProduto()
    return notaGroup.mapNotNull { (_, produtosChave) ->
      produtosChave.firstOrNull()?.let { nota ->
        val produtosValidos = produtosChave.map { ProdutoSaci(it.prdno, it.grade) }.filter { produto ->
          val dataCadastroProduto = produto.dataCadastro ?: return@filter false
          dataCadastroProduto <= nota.date.localDate()
        }.distinct()
        when {
          produtosValidos.isNotEmpty() -> NotaSaci(invno = nota.invno,
                                                   storeno = nota.storeno,
                                                   numero = nota.numero,
                                                   serie = nota.serie,
                                                   date = nota.date,
                                                   dtEmissao = nota.dtEmissao,
                                                   tipo = nota.tipo,
                                                   cancelado = nota.cancelado,
                                                   produtos = produtosValidos)
          else                         -> null
        }
      }
    }
  }

  fun findDevolucaoFornecedor(storeno: Int, abreviacao: String): List<DevolucaoFornecedor> {
    val sql = "/sqlSaci/findDevolucaoFornecedor.sql"
    return query(sql) { q ->
      q.addParameter("storeno", storeno)
        .addParameter("abreviacao", "${abreviacao}%")
        .executeAndFetch(DevolucaoFornecedor::class.java)
    }
  }

  fun findVendasCaixa(): List<VendasCaixa> {
    val sql = "/sqlSaci/findVendaCaixa.sql"
    val dataAtual = LocalDate.now().toSaciDate()
    return query(sql) { q ->
      q.addParameter("data_atual", "$dataAtual").executeAndFetch(VendasCaixa::class.java)
    }
  }

  fun findPedidoTransferencia(): List<PedidoSaci> {
    val sql = "/sqlSaci/findPedidoTransferencia.sql"
    val dataInicial = LocalDate.now().minusMonths(6).toSaciDate()
    return query(sql) { q ->
      q.addParameter("data_inicial", "$dataInicial").executeAndFetch(PedidoSaci::class.java)
    }
  }

  fun findEntregaFutura(): List<EntregaFutura> {
    val sql = "/sqlSaci/findEntragaFutura.sql"
    val dataInicial = LocalDate.now().minusMonths(6).toSaciDate()
    return query(sql) { q ->
      q.addParameter("data_inicial", "$dataInicial").executeAndFetch(EntregaFutura::class.java)
    }
  }

  fun findTransferenciaAutomatica(): List<TransferenciaAutomatica> {
    val sql = "/sqlSaci/findTransferenciaAutomatica.sql"
    val dataInicial = LocalDate.now().minusMonths(6).toSaciDate()
    return query(sql) { q ->
      q.addParameter("data_inicial", "$dataInicial").executeAndFetch(TransferenciaAutomatica::class.java)
    }
  }

  fun findPedidoNota(): List<PedidoNotaRessuprimento> {
    val sql = "/sqlSaci/pedidoNotaRessuprimento.sql"
    val dataInicial = LocalDate.now().minusMonths(6).toSaciDate()
    return query(sql) { q ->
      q.addParameter("data_inicial", "$dataInicial").executeAndFetch(PedidoNotaRessuprimento::class.java)
    }
  }

  fun findDadosProdutosSaci(): List<DadosProdutosSaci> {
    val sql = "/sqlSaci/tab_produtos.sql"
    return query(sql) { q ->
      q.executeAndFetch(DadosProdutosSaci::class.java)
    }
  }

  fun expiraPedidoVenda(storeno: Int?, ordno: Int?) {
    storeno ?: return
    ordno ?: return
    val sql = "/sqlSaci/expiraPedidoVenda.sql"
    script(sql) { q ->
      q.addOptionalParameter("storeno", storeno)
      q.addOptionalParameter("ordno", ordno)
      q.executeUpdate()
    }
  }

  fun findProdutoGarantia(codigo : String?): ProdutoGarantia? {
    codigo ?: return null
    val sql = "/sqlSaci/produtoGrantia.sql"
    return query(sql) { q ->
      q.addOptionalParameter("codigo", codigo.trim())
      q.executeAndFetch(ProdutoGarantia::class.java).firstOrNull()
    }
  }

  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer = url.split("/").getOrNull(2)
  }

  private data class KeyNota(val storeno: Int, val numero: String, val serie: String)
}

val saci = QuerySaci()