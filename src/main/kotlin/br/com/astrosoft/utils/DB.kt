package br.com.astrosoft.utils

import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class DB(banco: String) {
  private val prop = properties()
  val driver = prop?.getProperty("datasource.$banco.databaseDriver") ?: ""
  val url = prop?.getProperty("datasource.$banco.databaseUrl") ?: ""
  val username = prop?.getProperty("datasource.$banco.username") ?: ""
  val password = prop?.getProperty("datasource.$banco.password") ?: ""
  val test = prop?.getProperty("test") == "true"
  
  companion object {
    private val propertieFile = System.getProperty("ebean.props.file")
    
    private fun properties(): Properties? {
      val properties = Properties()
      val file = File(propertieFile)
      
      properties.load(FileReader(file))
      return properties
    }
  }
}

fun parameterNames(sql: String): List<String> {
  val regex = Regex(":([a-zA-Z0-9_]+)")
  val matches = regex.findAll(sql)
  return matches.map {it.groupValues}
    .toList()
    .flatten()
    .filter {!it.startsWith(":")}
}

@Suppress("UNCHECKED_CAST")
fun <T: Any, R: Any> readInstanceProperty(instance: T, propertyName: String): R? {
  val property = instance::class.memberProperties.firstOrNull {it.name == propertyName} as? KProperty1<T, R>
                 ?: return null
  return property.get(instance)
}
