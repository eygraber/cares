import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

buildscript {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  dependencies {
    classpath(kotlin("gradle-plugin", version = "1.4.32"))
    classpath(kotlin("serialization", version = "1.4.32"))
    classpath("com.android.tools.build:gradle:7.0.0-alpha14")
    classpath("org.jetbrains.compose:compose-gradle-plugin:0.4.0-build185")
    classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.15.2")
    classpath("com.vanniktech:gradle-maven-publish-plugin:0.15.1")
    classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
  }
}

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
          jvmTarget = JavaVersion.VERSION_1_8.toString()
          compileKotlinTask.sourceCompatibility = JavaVersion.VERSION_1_8.toString()
          compileKotlinTask.targetCompatibility = JavaVersion.VERSION_1_8.toString()
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
