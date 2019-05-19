package br.com.engecopi.framework.model

import io.ebean.migration.MigrationConfig
import io.ebean.migration.MigrationRunner
import java.io.File
import java.util.*

fun main() {
  // System.setProperty("ddl.migration.generate", "true")
  val home = System.getenv("HOME")
  val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
  val properties = Properties()
  properties.load(File(fileName).reader())
  val config = MigrationConfig()
  config.dbUsername = properties.getProperty("datasource.db.username")
  config.dbPassword = properties.getProperty("datasource.db.password")
  config.dbDriver = properties.getProperty("datasource.db.databaseDriver")
  config.dbUrl = properties.getProperty("datasource.db.databaseUrl")
  config.isSkipChecksum = true
  val runner = MigrationRunner(config)
  runner.run()
}

