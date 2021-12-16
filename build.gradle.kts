import org.danilopianini.gradle.mavencentral.JavadocJar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    api(libs.json)
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    testImplementation(libs.awaitility)
    testImplementation(libs.json)
    testImplementation(libs.spek.dsl.jvm)
    testRuntimeOnly(libs.spek.runner.junit5)
}

tasks.withType<JavadocJar> {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.get().outputDirectory)
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

group = "org.danilopianini" // This must be configured for the generated pom.xml to work correctly

publishOnCentral {
    projectDescription.set("A HTTP request library for Kotlin.")
    projectLongName.set("khttp")
    licenseName.set("Mozilla Public License v. 2.0")
    licenseUrl.set("https://mozorg.cdn.mozilla.net/media/MPL/2.0/index.815ca599c9df.txt")
}

publishing {
    publications {
        withType<MavenPublication>() {
            pom {
                developers {
                    developer {
                        name.set("Kyle Clemens")
                        email.set("me@kyleclemens.com")
                    }
                    developer {
                        name.set("Danilo Pianini")
                        email.set("danilo.pianini@gmail.com")
                        url.set("http://www.danilopianini.org/")
                    }
                }
            }
        }
    }
}
