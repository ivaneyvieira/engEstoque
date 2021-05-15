package br.com.engecopi.framework.model

import io.ebean.typequery.generator.GenerationMetaData
import io.ebean.typequery.generator.Generator
import io.ebean.typequery.generator.GeneratorConfig
import io.ebean.typequery.generator.read.EntityBeanPropertyReader
import io.ebean.typequery.generator.write.SimpleFinderWriter

class GeneratorKotlin(val config: GeneratorConfig) : Generator(config) {
  override fun generateFinder(classMeta: EntityBeanPropertyReader?) {
    val generationMetaData = GenerationMetaData(config)
    val writer = SimpleFinderWriter2(config, classMeta, generationMetaData)
    if (writer.write()) { //logger.debug("... generated finder for {}", classMeta!!.name)
      finders.add(classMeta?.name)
    }
  }
}

class SimpleFinderWriter2(
  val config: GeneratorConfig?,
  val classMeta: EntityBeanPropertyReader?,
  val generationMetaData: GenerationMetaData?,
                         ) : SimpleFinderWriter(config, classMeta, generationMetaData) {
  override fun writeClass() {
    super.writeClass()
    writer.append("{")
    writer.append(NEWLINE)
  }

  override fun writeClassEnd() {
    super.writeClassEnd()
    writer.append("}")
    writer.append(NEWLINE)
  }
}