import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id ("org.jetbrains.kotlin.jvm")
}

repositories {
  mavenCentral()
  flatDir {
    dirs("libs")
    }
}

dependencies {
    implementation( "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile( files("libs/NBioBSPJNI.jar"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = "1.8"
}

