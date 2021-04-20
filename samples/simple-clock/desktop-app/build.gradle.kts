import org.jetbrains.compose.compose

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("org.jetbrains.compose")
}

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {

      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(project(":samples:simple-clock:app"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "com.eygraber.cure.samples.simple_clock.DesktopAppKt"
  }
}
