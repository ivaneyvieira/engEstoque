package br.com.engecopi.saci

import br.com.engecopi.saci.beans.LojaSaci
import br.com.engecopi.saci.beans.NfsKey
import br.com.engecopi.saci.beans.NotaSaci
import br.com.engecopi.saci.beans.UserSaci
import br.com.engecopi.utils.DB
import br.com.engecopi.utils.localDate
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
    val data = notaSaci.dt_emissao?.localDate() ?: return false
    val dataLimite =
      LocalDate.now()
        .minusDays(15)
    return data.isAfter(dataLimite)
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

  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    val ipServer =
      url.split("/")
        .getOrNull(2)
  }
}

val saci = QuerySaci()