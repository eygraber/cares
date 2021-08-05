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
        languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
      }

      jvm().compilations.all {
        kotlinOptions {
          jvmTarget = JavaVersion.VERSION_11.toString()
          compileKotlinTask.sourceCompatibility = JavaVersion.VERSION_11.toString()
          compileKotlinTask.targetCompatibility = JavaVersion.VERSION_11.toString()
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
