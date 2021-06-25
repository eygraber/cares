plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("org.jetbrains.compose")
  detekt
  `detekt-hotfix`
}

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":core"))

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
        implementation("com.soywiz.korlibs.klock:klock:2.1.2")

        implementation("com.eygraber:compose-color-picker:0.0.2-SNAPSHOT")

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
