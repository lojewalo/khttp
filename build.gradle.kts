import org.danilopianini.gradle.mavencentral.JavadocJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
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
    maven {
        url = uri("https://dl.bintray.com/kotlin/dokka")
        content {
            includeGroup("org.jetbrains.dokka")
        }
    }
}

gitSemVer {
    version = computeGitSemVer()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.json:json:_")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    testImplementation("org.json:json:_")
    testImplementation("org.jetbrains.spek:spek-api:_")
    testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:_")
}

group = "org.danilopianini" // This must be configured for the generated pom.xml to work correctly

tasks.withType<DokkaTask> {
    // Workaround for https://github.com/Kotlin/dokka/issues/294
    outputFormat = if (JavaVersion.current().isJava10Compatible) "html" else "javadoc"
    outputDirectory = "$buildDir/javadoc"
    tasks.withType<JavadocJar> {
        from(outputDirectory)
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

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
