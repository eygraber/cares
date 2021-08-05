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
        api(projects.core)
        api(projects.nav)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization)

        api(compose.runtime)
        api(compose.foundation)
        api(compose.material)
        api(compose.materialIconsExtended)

        implementation("com.soywiz.korlibs.klock:klock:2.1.2")

        implementation("com.eygraber:compose-color-picker:0.0.2-SNAPSHOT")
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
