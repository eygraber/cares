import org.jetbrains.compose.compose

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
        implementation(project(":nav"))
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(projects.samples.simpleClock.app)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization)

        implementation(compose.desktop.currentOs)
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "com.eygraber.cares.samples.simple_clock.DesktopAppKt"
  }
}
