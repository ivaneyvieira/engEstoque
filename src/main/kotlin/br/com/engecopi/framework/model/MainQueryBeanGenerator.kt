package br.com.engecopi.framework.model

import io.ebean.typequery.generator.GeneratorConfig
import io.ebean.typequery.generator.GeneratorConfig.LANG_KOTLIN

fun main() {
  val config = GeneratorConfig()
  config.lang = LANG_KOTLIN
  config.classesDirectory = "./out/production/classes/"
  //config.classesDirectory = "./build/classes/kotlin/main/"
  config.destDirectory = "./src/main/kotlin"
  config.destResourceDirectory = "./src/main/resources"
  
  config.entityBeanPackage = "br.com.engecopi.estoque.model"
  config.destPackage = "br.com.engecopi.estoque.model.query"
  
  config.isAddFinderTextMethod = false
  config.isAddFinderWherePublic = false
  
  config.isOverwriteExistingFinders = true
  val generator = GeneratorKotlin(config)
  generator.generateQueryBeans()
  // Additionally generate 'finder's
  generator.generateFinders()
  generator.modifyEntityBeansAddFinderField()
}

