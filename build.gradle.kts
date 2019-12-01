import io.ebean.gradle.EnhancePluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = properties["kotlinVersion"] as String
val karibuVersion = properties["karibuVersion"] as String
val vaadin8Version = properties["vaadin8Version"] as String
/*
buildscript {
  val kotlinVersion: String by project
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  }
}
*/
plugins {
  id("org.jetbrains.kotlin.jvm") version "1.3.40"
  id("org.gretty") version "2.3.1"
  id("com.devsoap.plugin.vaadin") version "2.0.0.beta2"
  id("io.ebean") version "12.1.5"
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
  version = "8.9.2"
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
  implementation("com.github.mvysny.karibudsl:karibu-dsl-v8:$karibuVersion")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("org.slf4j:slf4j-api:1.7.25")
  
  implementation("org.slf4j:jul-to-slf4j:1.7.25")
  
  testImplementation("com.github.mvysny.dynatest:dynatest:0.8")
  
  implementation("io.ebean:ebean:12.1.3")
  implementation("io.ebean:ebean-querybean:12.1.3")
  implementation("io.ebean:ebean-agent:12.1.3")
  implementation("io.ebean.tools:finder-generator:11.34.2")
  
  implementation("com.vaadin:vaadin-themes:$vaadin8Version")
  implementation("com.vaadin:vaadin-server:$vaadin8Version")
  implementation("com.vaadin:vaadin-client-compiled:$vaadin8Version")
  implementation("javax.servlet:javax.servlet-api:3.1.0")
  
  implementation("mysql:mysql-connector-java:5.1.48")
  
  implementation("org.apache.commons:commons-dbcp2:2.3.0")
  
  implementation("org.cups4j:cups4j:0.7.6")
  implementation("org.glassfish.jersey.core:jersey-client:2.27")
  implementation("org.glassfish.jersey.media:jersey-media-multipart:2.27")
  implementation("org.glassfish.jersey.inject:jersey-hk2:2.27")
  implementation("khttp:khttp:1.0.0")
  
  implementation("org.xerial:sqlite-jdbc:3.21.0.1")
  implementation("org.sql2o:sql2o:1.5.4")
  // https://mvnrepository.com/artifact/com.jolbox/bonecp
  //implementation("com.jolbox:bonecp:0.8.0.RELEASE")
  implementation("com.zaxxer:HikariCP:3.4.1")
  
  implementation("org.imgscalr:imgscalr-lib:4.2")
  implementation("de.steinwedel.vaadin.addon:messagebox:4.0.21")
  implementation("org.vaadin.patrik:GridFastNavigation:2.4.8")
  implementation("org.vaadin:viritin:2.9")
  //implementation("org.vaadin.crudui:crudui:2.3.1")
  implementation("org.vaadin.addons:filtering-grid:0.1.1")
  implementation("com.fo0.advancedtokenfield:AdvancedTokenField:0.5.1")
  implementation("org.vaadin:grideditorcolumnfix:0.3.1")
  implementation("com.whitestein.vaadin.widgets:wt-pdf-viewer:2.0.1")
  //implementation("org.vaadin:grid-renderers-collection-addon:2.6.0")
  // heroku app runner
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}



