plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("org.jetbrains.compose")
  id("kotlinx-atomicfu")
}

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

        api("org.jetbrains.kotlinx:atomicfu:0.15.2")

        api(compose.runtime)
        api(compose.foundation)
        api(compose.material)
        api(compose.materialIconsExtended)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}

apply(from = File(rootDir, "publishing.gradle"))
