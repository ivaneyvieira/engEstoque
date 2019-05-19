package br.com.engecopi.framework.model

import io.ebean.Ebean
import io.ebean.Query
import io.ebean.SqlQuery
import io.ebean.SqlUpdate

object Transaction {
  private fun inTransaction(): Boolean {
    return Ebean.currentTransaction() != null
  }

  fun execTransacao(lambda: () -> Unit) {
    if(inTransaction()) lambda()
    else Ebean.beginTransaction().use {transaction ->
      lambda()
      transaction.commit()
    }
  }

  fun variable(name: String, value: String?) {
    Ebean.currentTransaction()
      ?.connection?.let {con ->
      val stmt = con.createStatement()
      val sql = "SET @$name := $value;"
      stmt.executeQuery(sql)
    }
  }

  fun createSqlUpdate(sql: String): SqlUpdate? {
    return Ebean.createSqlUpdate(sql)
  }

  fun <T> find(javaClass: Class<T>): Query<T>? {
    return Ebean.find(javaClass)
  }

  fun createSqlQuery(sql: String): SqlQuery? {
    return Ebean.createSqlQuery(sql)
  }
}
