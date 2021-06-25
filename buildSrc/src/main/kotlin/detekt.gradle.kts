import io.gitlab.arturbosch.detekt.Detekt

plugins {
  id("io.gitlab.arturbosch.detekt")
}

apply(from = rootProject.file("buildSrc/versions.gradle"))

val detektVersion = project.findProperty("detektVersion") as String

detekt {
  toolVersion = detektVersion

  input.from("build.gradle.kts")

  autoCorrect = true
  parallel = true

  buildUponDefaultConfig = true

  config = project.files("${project.rootDir}/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
  // Target version of the generated JVM bytecode. It is used for type resolution.
  jvmTarget = "11"
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
  detektPlugins("com.eygraber.detekt.rules:formatting:1.0.10")
  detektPlugins("com.eygraber.detekt.rules:style:1.0.10")
}
