
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.ebean.gradle.EnhancePluginExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val kotlinVersion = properties["kotlinVersion"] as String
val karibuVersion = properties["karibuVersion"] as String
val vaadin8Version = properties["vaadin8Version"] as String


plugins {
  id("org.jetbrains.kotlin.jvm") version "1.3.60"
  id("org.gretty") version "2.3.1"
  id("com.devsoap.plugin.vaadin") version "1.4.1"
  id("io.ebean") version "12.1.5"
  id("com.github.johnrengelman.shadow") version "5.1.0"
  war
}

repositories {
  mavenCentral()
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
}

defaultTasks("clean", "build")

group = "engEstoque"
version = "1.0-SNAPSHOT"

gretty {
  contextPath = "/"
  servletContainer = "jetty9.4"
}

vaadin {
  version = "8.9.3"
}

configure<EnhancePluginExtension> {
  debugLevel = 1
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = "1.8"
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    showStandardStreams = true
    events = setOf(PASSED, FAILED, SKIPPED)
  }
  reports.junitXml.isEnabled = false
  reports.html.isEnabled = true
}

dependencies {
  // Karibu-DSL dependency
  compile("com.github.mvysny.karibudsl:karibu-dsl-v8:$karibuVersion")
  compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compile("org.jetbrains.kotlin:kotlin-reflect")
  
  compile("ch.qos.logback:logback-classic:1.2.3")
  compile("org.slf4j:slf4j-api:1.7.25")
  
  compile("org.slf4j:jul-to-slf4j:1.7.25")
  
  testImplementation("com.github.mvysny.dynatest:dynatest:0.8")
  
  compile("io.ebean:ebean:12.1.5")
  compile("io.ebean:ebean-querybean:12.1.5")
  compile("io.ebean:ebean-agent:12.1.5")
 // testImplementation("io.ebean:ebean-test:12.1.5")
  
  compile("io.ebean.tools:finder-generator:12.1.1")
  
  compile("com.vaadin:vaadin-themes:$vaadin8Version")
  compile("com.vaadin:vaadin-server:$vaadin8Version")
  compile("com.vaadin:vaadin-client-compiled:$vaadin8Version")
  compile("javax.servlet:javax.servlet-api:3.1.0")
  
  compile("mysql:mysql-connector-java:5.1.48")
  
  compile("org.apache.commons:commons-dbcp2:2.3.0")
  
  compile("org.cups4j:cups4j:0.7.6")
  compile("org.glassfish.jersey.core:jersey-client:2.27")
  compile("org.glassfish.jersey.media:jersey-media-multipart:2.27")
  compile("org.glassfish.jersey.inject:jersey-hk2:2.27")
  compile("khttp:khttp:1.0.0")
  
  compile("org.xerial:sqlite-jdbc:3.21.0.1")
  compile("org.sql2o:sql2o:1.5.4")
  // https://mvnrepository.com/artifact/com.jolbox/bonecp
  //compile("com.jolbox:bonecp:0.8.0.RELEASE")
  compile("com.zaxxer:HikariCP:3.4.1")
  
  compile("org.imgscalr:imgscalr-lib:4.2")
  compile("de.steinwedel.vaadin.addon:messagebox:4.0.21")
  compile("org.vaadin.patrik:GridFastNavigation:2.4.8")
  compile("org.vaadin:viritin:2.9")
  //compile("org.vaadin.crudui:crudui:2.3.1")
  compile("org.vaadin.addons:filtering-grid:0.1.1")
  compile("com.fo0.advancedtokenfield:AdvancedTokenField:0.5.1")
  compile("org.vaadin:grideditorcolumnfix:0.3.1")
  compile("com.whitestein.vaadin.widgets:wt-pdf-viewer:2.0.1")
  //compile("org.vaadin:grid-renderers-collection-addon:2.6.0")
  // heroku app runner
  testImplementation("org.assertj:assertj-core:3.12.2")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

tasks.getByName<Jar>("jar") {
  enabled = true
}

tasks.getByName<War>("war") {
  enabled = true
}

tasks.withType<ShadowJar>() {
  manifest {
    attributes["Main-Class"] = "br.com.engecopi.estoque.background.EtlExecuteKt"
  }
}