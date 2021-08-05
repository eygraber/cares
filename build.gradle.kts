import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      sourceCompatibility = JavaVersion.VERSION_11.toString()
      targetCompatibility = JavaVersion.VERSION_11.toString()
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
  }

  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    with(extensions.getByType<KotlinMultiplatformExtension>()) {
      sourceSets.configureEach {
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
