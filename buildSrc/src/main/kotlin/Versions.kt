import org.gradle.api.Project

val Project.kotlinVersion get() = findProperty("kotlinVersion") as String
