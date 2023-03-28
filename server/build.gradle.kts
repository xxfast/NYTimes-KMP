plugins {
  kotlin("jvm")
  id("io.ktor.plugin") version "2.2.4"
  kotlin("plugin.serialization")
}

application {
  mainClass.set("io.github.xxfast.nytimes.server.ApplicationKt")

  val isDevelopment: Boolean = project.ext.has("development")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
  implementation(project(":core"))
  implementation("com.arkivanov.essenty:parcelable:1.0.0")
  implementation("io.ktor:ktor-server-core-jvm:2.2.4")
  implementation("io.ktor:ktor-server-call-logging-jvm:2.2.4")
  implementation("io.ktor:ktor-server-content-negotiation-jvm:2.2.4")
  implementation("io.ktor:ktor-client-core:2.2.4")
  implementation("io.ktor:ktor-client-content-negotiation:2.2.4")
  implementation("io.ktor:ktor-client-logging:2.2.4")
  implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.2.4")
  implementation("io.ktor:ktor-server-cio-jvm:2.2.4")
  implementation("ch.qos.logback:logback-classic:1.2.11")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

  testImplementation("io.ktor:ktor-server-tests-jvm:2.2.4")
  testImplementation(kotlin("test"))
}