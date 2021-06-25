plugins {
  `kotlin-dsl`
}

apply(from = project.file("versions.gradle"))

val detektVersion = project.findProperty("detektVersion") as String

repositories {
  google()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation(kotlin("gradle-plugin", version = "1.5.10"))
  implementation("com.android.tools.build:gradle:7.0.0-beta03")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion")
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.16.0")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
  implementation("org.jetbrains.compose:compose-gradle-plugin:0.5.0-build226")
  implementation("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.16.1")
}
