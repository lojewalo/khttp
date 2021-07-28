plugins {
    `kotlin-dsl`
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:_")
}

detekt {
    allRules = true
    buildUponDefaultConfig = true
    config = files("$rootDir/../config/detekt/detekt.yml")
}

projectDir.listFiles()
    ?.filter { it.isFile && it.name.endsWith("dependencies") }
    ?.flatMap { it.readLines() }
    ?.forEach {
        dependencies {
            implementation(it)
        }
    }
