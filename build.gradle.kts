import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

repositories {
  google()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
  }

  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    with(extensions.getByType<KotlinMultiplatformExtension>()) {
      sourceSets.all {
        languageSettings.optIn("kotlin.RequiresOptIn")
      }

      jvm().compilations.all {
        kotlinOptions {
          jvmTarget = libs.versions.jdk.get()
          compileKotlinTask.sourceCompatibility = libs.versions.jdk.get()
          compileKotlinTask.targetCompatibility = libs.versions.jdk.get()
        }
      }

      targets.all {
        compilations.all {
          kotlinOptions {
            allWarningsAsErrors = true
            freeCompilerArgs = emptyList()
          }
        }
      }
    }
  }
}
