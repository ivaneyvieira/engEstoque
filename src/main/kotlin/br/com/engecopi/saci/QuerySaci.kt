package br.com.engecopi.saci

import br.com.engecopi.estoque.model.Nota
import br.com.engecopi.saci.beans.ChaveProduto
import br.com.engecopi.saci.beans.LojaSaci
import br.com.engecopi.saci.beans.NfsKey
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.saci.beans.UserSaci
import br.com.engecopi.saci.beans.findChave
import br.com.engecopi.utils.DB
import br.com.engecopi.utils.localDate
import br.com.engecopi.utils.lpad
import java.time.LocalDate

class QuerySaci: QueryDB(driver, url, username, password) {
  fun findNotaEntrada(storeno: Int, nfname: String, invse: String, liberaNotasAntigas: Boolean): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaEntrada.sql"
    return if(nfname == "") emptyList()
    else query(sql) {q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfname", nfname)
        .addParameter("invse", invse)
        .executeAndFetch(NotaSaci::class.java)
    }.filter {liberaNotasAntigas || filtroDataRecente(it)}
  }

  fun findNotaSaida(storeno: Int, nfno: String, nfse: String, liberaNotasAntigas: Boolean): List<NotaSaci> {
    return if(nfno == "") emptyList()
    else if(nfse == "") findNotaSaidaOrd(storeno, nfno)
    else {
      val nfs = findNotaSaidaNF(storeno, nfno, nfse)
      if(nfs.isNotEmpty()) nfs
      else findNotaSaidaPxa(storeno, nfno, nfse)
    }.filter {liberaNotasAntigas || filtroDataRecente(it)}
  }

  private fun filtroDataRecente(notaSaci: NotaSaci): Boolean {
    val dtEmissao = notaSaci.dtEmissao?.localDate() ?: return false
    val date = notaSaci.date?.localDate() ?: return false
    val dataLimite = LocalDate.now()
      .minusDays(45)
    return date.isAfter(dataLimite) || dtEmissao.isAfter(dataLimite)
  }

  private fun findNotaSaidaNF(storeno: Int, nfno: String, nfse: String): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaSaidaNF.sql"
    return query(sql) {q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfno", nfno)
        .addParameter("nfse", nfse)
        .executeAndFetch(NotaSaci::class.java)
    }
  }

  private fun findNotaSaidaOrd(storeno: Int, nfno: String): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaSaidaOrd.sql"
    return query(sql) {q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfno", nfno)
        .executeAndFetch(NotaSaci::class.java)
    }
  }

  private fun findNotaSaidaPxa(storeno: Int, nfno: String, nfse: String): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaSaidaPXA.sql"
    return query(sql) {q ->
      q.addParameter("storeno", storeno)
        .addParameter("nfno", nfno)
        .addParameter("nfse", nfse)
        .executeAndFetch(NotaSaci::class.java)
    }
  }

  fun findLojas(storeno: Int): List<LojaSaci> {
    val sql = "/sqlSaci/findLojas.sql"
    return query(sql) {q ->
      q.executeAndFetch(LojaSaci::class.java)
        .filter {it.storeno == storeno || storeno == 0}
    }
  }

  fun findUser(login: String): UserSaci? {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql) {q ->
      q.addParameter("login", login)
        .executeAndFetch(UserSaci::class.java)
        .firstOrNull()
    }
  }

  fun findLoginUser(): List<String> {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql) {q ->
      q.addParameter("login", "TODOS")
        .executeAndFetch(UserSaci::class.java)
        .mapNotNull {it.login}
    }
  }

  fun findNotaSaidaKey(nfeKey: String, liberaNotasAntigas: Boolean): List<NotaSaci> {
    val sql = "/sqlSaci/findNotaSaidaKey.sql"
    return query(sql) {q ->
      q.addParameter("nfekey", nfeKey)
        .executeAndFetch(NfsKey::class.java)
        .firstOrNull()
    }?.let {key ->
      findNotaSaida(key.storeno, key.nfno, key.nfse, liberaNotasAntigas)
    } ?: emptyList()
  }

  fun findBarcode(storeno: Int, barcode: String): List<ChaveProduto> {
    val sql = "/sqlSaci/findBarcode.sql"
    return query(sql) {q ->
      q.addParameter("storeno", storeno)
        .addParameter("barcode", barcode.lpad(16, " "))
        .executeAndFetch(ChaveProduto::class.java)
        .findChave()
    }
  }

  fun findBarcode(storeno: Int, prdno: String, grade: String): List<ChaveProduto> {
    val sql = "/sqlSaci/findBarcode2.sql"
    return query(sql) {q ->
      q.addParameter("storeno", storeno)
        .addParameter("prdno", prdno)
        .addParameter("grade", grade)
        .executeAndFetch(ChaveProduto::class.java)
        .findChave()
    }
  }

  fun findFornecedor(vendno: Int): List<ChaveProduto> {
    val sql = "/sqlSaci/findFornecedor.sql"
    return query(sql) {q ->
      q.addParameter("vendno", vendno)
        .executeAndFetch(ChaveProduto::class.java)
    }
  }

  fun findCentroLucro(clno: Int): List<ChaveProduto> {
    val sql = "/sqlSaci/findCentroLucro.sql"
    return query(sql) {q ->
      q.addParameter("clno", clno)
        .executeAndFetch(ChaveProduto::class.java)
    }
  }

  fun findTipoProduto(typeno: Int): List<ChaveProduto> {
    val sql = "/sqlSaci/findTipoProduto.sql"
    return query(sql) {q ->
      q.addParameter("typeno", typeno)
        .executeAndFetch(ChaveProduto::class.java)
    }
  }

  fun findNotasSaidaCancelada(lista: List<Nota>): List<Nota> {
    val sqlTemp = temporaryTable("TempNotaSaida", lista) {nota ->
      val storeno = nota.loja?.numero ?: 0
      val nfno = nota.numero.split("/").getOrNull(0) ?: ""
      val nfse = nota.numero.split("/").getOrNull(1) ?: ""
      "$storeno AS storeno, '$nfno' AS nfno, '$nfse' AS nfse"
    }
    println(sqlTemp)
    return lista
  }

  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer = url.split("/")
      .getOrNull(2)
  }
}

val saci = QuerySaci()