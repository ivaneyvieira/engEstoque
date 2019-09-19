import io.ebean.gradle.EnhancePluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = properties["kotlinVersion"] as String
val karibuVersion = properties["karibuVersion"] as String
val vaadin8Version = properties["vaadin8Version"] as String
buildscript {
  val kotlinVersion: String by project
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    classpath("io.ebean:ebean-gradle-plugin:11.37.1")
  }
}


plugins {
  id("org.jetbrains.kotlin.jvm") version "1.3.40"
  id("org.gretty") version "2.3.1"
  id("com.devsoap.plugin.vaadin") version "1.4.1"
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

apply(plugin = "war")
apply(plugin = "kotlin")
apply(plugin = "io.ebean")

gretty {
  contextPath = "/"
  servletContainer = "jetty9.4"
}

vaadin {
  version = "8.8.5"
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

tasks.named<Test>("test") {
  useJUnitPlatform()
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

  compile("io.ebean:ebean:11.41.1")
  // compile "io.ebean:querybean-generator:11.37.1"
  compile("io.ebean:ebean-querybean:11.40.1")
  implementation("io.ebean:ebean-agent:11.41.1")
  

  //compile "io.ebean:ebean-anebean-agent notation:4.7"
  compile("io.ebean.tools:finder-generator:11.34.1")

  compile("com.vaadin:vaadin-themes:$vaadin8Version")
  compile("com.vaadin:vaadin-server:$vaadin8Version")
  compile("com.vaadin:vaadin-client-compiled:$vaadin8Version")
  compile("javax.servlet:javax.servlet-api:3.1.0")

  compile("mysql:mysql-connector-java:5.1.41")

  compile("org.apache.commons:commons-dbcp2:2.3.0")

  compile("org.cups4j:cups4j:0.7.6")
  compile("org.glassfish.jersey.core:jersey-client:2.27")
  compile("org.glassfish.jersey.media:jersey-media-multipart:2.27")
  compile("org.glassfish.jersey.inject:jersey-hk2:2.27")
  compile("khttp:khttp:1.0.0")

  compile("org.xerial:sqlite-jdbc:3.21.0.1")
  compile("org.sql2o:sql2o:1.5.4")
  // https://mvnrepository.com/artifact/com.jolbox/bonecp
  compile("com.jolbox:bonecp:0.8.0.RELEASE")

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
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}



