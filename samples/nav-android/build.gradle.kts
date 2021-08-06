import org.jetbrains.compose.compose

plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose")
  kotlin("plugin.serialization")
  detekt
  `detekt-hotfix`
}

android {
  compileSdk = 30

  defaultConfig {
    targetSdk = 30
    minSdk = 21

    applicationId = "com.eygraber.cares.samples.nav.android"

    versionCode = 1
    versionName = "1.0.0"

    multiDexEnabled = true
  }

  compileOptions {
    // isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  implementation(projects.core)
  implementation(projects.nav)

  implementation(compose.material)
  implementation(libs.androidx.activityCompose)
  implementation(libs.androidx.appCompat)
  implementation(libs.kotlinx.serialization)
}
