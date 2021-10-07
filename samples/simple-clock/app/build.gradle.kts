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

        implementation(libs.sample.klock)

        implementation(libs.sample.colorPicker)
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
