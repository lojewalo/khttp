import org.danilopianini.gradle.mavencentral.JavadocJar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    signing
    `maven-publish`
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("org.jetbrains.dokka")
    id("org.danilopianini.publish-on-central")
}

repositories {
    mavenCentral()
    jcenter {
        content {
            onlyForConfigurations(
                "dokkaJavadoc",
                "dokkaJavadocPlugin",
                "dokkaJavadocRuntime",
                "testCompileClasspath",
                "testRuntimeClasspath"
            )
        }
    }
}

gitSemVer {
    version = computeGitSemVer()
}

dependencies {
    api(kotlin("stdlib"))
    api("org.json:json:_")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    testImplementation("org.awaitility:awaitility-kotlin:_")
    testImplementation("org.json:json:_")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:_")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:_")
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
    projectDescription = "A HTTP request library for Kotlin."
    projectLongName = "khttp"
    licenseName = "Mozilla Public License v. 2.0"
    licenseUrl = "https://mozorg.cdn.mozilla.net/media/MPL/2.0/index.815ca599c9df.txt"
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
