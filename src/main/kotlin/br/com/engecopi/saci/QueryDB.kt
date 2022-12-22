package br.com.engecopi.saci

import br.com.engecopi.utils.SystemUtils
import br.com.engecopi.utils.SystemUtils.readFile
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.sql2o.Connection
import org.sql2o.Query
import org.sql2o.Sql2o

open class QueryDB(private val driver: String, val url: String, val username: String, val password: String) {
  private val sql2o: Sql2o

  init {
    registerDriver(driver)
    val config = HikariConfig()
    config.jdbcUrl = url
    config.username = username
    config.password = password
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("useServerPrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    val ds = HikariDataSource(config)
    ds.maximumPoolSize = 5

    this.sql2o = Sql2o(ds) //this.sql2o = Sql2o(url, username, password)
  }

  private fun registerDriver(driver: String) {
    try {
      Class.forName(driver)
    } catch (e: ClassNotFoundException) {
      throw RuntimeException(e)
    }
  }

  protected fun <T> query(file: String, lambda: (Query) -> T): T {
    return buildQuery(file) { con, query ->
      val ret = lambda(query)
      con.close()
      ret
    }
  }

  private inline fun <C : AutoCloseable, R> C.trywr(block: (C) -> R): R {
    this.use {
      return block(this)
    }
  }

  protected fun execute(file: String,
                        vararg params: Pair<String, String>,
                        monitor: (String, Int, Int) -> Unit = { _, _, _ -> }) {
    var sqlScript = SystemUtils.readFile(file)
    sql2o.beginTransaction().trywr { con ->
      params.forEach {
        sqlScript = sqlScript?.replace(":${it.first}", it.second)
      }
      val sqls = sqlScript?.split(";").orEmpty()
      val count = sqls.size
      sqls.filter { it.trim() != "" }.forEachIndexed { index, sql ->
        println(sql)
        val query = con.createQuery(sql)
        query.executeUpdate()
        val parte = index + 1
        val caption = "Parte $parte/$count"
        monitor(caption, parte, count)
      }
      monitor("", count, count)
      con.commit()
    }
  }

  private fun <T> buildQuery(file: String, proc: (Connection, Query) -> T): T {
    val sql = SystemUtils.readFile(file)
    return this.sql2o.open().use { con ->
      val query = con.createQuery(sql)
      val time = System.currentTimeMillis()
      println("SQL2O ==> $sql")
      val result = proc(con, query)
      val difTime = System.currentTimeMillis() - time
      println("######################## TEMPO QUERY $difTime ms ########################")
      result
    }
  }

  protected fun <T> temporaryTable(tableName: String, lista: List<T>, fieldList: (T) -> String): String {
    val stringBuild = StringBuilder()
    val selectList = lista.joinToString(separator = "\nUNION\n") { item ->
      "SELECT ${fieldList(item)} FROM DUAL"
    }
    stringBuild.append("""
        DROP TABLE IF EXISTS $tableName;
        CREATE TEMPORARY TABLE $tableName
        $selectList;
      """.trimIndent())
    return stringBuild.toString()
  }

  fun Query.addOptionalParameter(name: String, value: String?): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Int): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  fun Query.addOptionalParameter(name: String, value: Double): Query {
    if (this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }

  protected fun script(file: String, lambda: (Query) -> Unit) {
    val stratments =
      readFile(file)?.split(";").orEmpty().map { it.trim() }.filter { it.isNotBlank() || it.isNotEmpty() }
    transaction { con ->
      stratments.forEach { sql ->
        val query = con.createQuery(sql)
        lambda(query)
      }
    }
  }

  protected fun <T> scriptQuery(file: String, lambda: (Query, Boolean) -> T): T {
    val stratments =
      readFile(file)?.split(";").orEmpty().map { it.trim() }.filter { it.isNotBlank() || it.isNotEmpty() }
    val script = stratments.subList(0, stratments.size - 1)
    val sql = stratments.last()
    return transaction { con ->
      script.forEach { sql ->
        val query = con.createQuery(sql)
        lambda(query, false)
      }
      val query = con.createQuery(sql)
      val time = System.currentTimeMillis()
      println("SQL2O ==> $sql")
      val ret = lambda(query, true)
      val difTime = System.currentTimeMillis() - time
      println("######################## TEMPO QUERY $difTime ms ########################")
      return@transaction ret
    }
  }

  private fun <T> transaction(block: (Connection) -> T): T {
    return sql2o.beginTransaction().use { con ->
      val ret = block(con)
      con.commit()
      ret
    }
  }
}
