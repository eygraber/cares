import org.gradle.api.Project

val Project.atomicFuVersion get() = findProperty("atomicFuVersion") as String
val Project.kotlinVersion get() = findProperty("kotlinVersion") as String
