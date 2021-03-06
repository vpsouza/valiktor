import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.2.51"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "0.9.17"
    id("com.adarshr.test-logger") version "1.3.1"
    id("org.jmailen.kotlinter") version "1.16.0"
}

subprojects {
    fun DependencyHandler.assertj(module: String) = "org.assertj:assertj-$module:3.9.1"
    fun DependencyHandler.junit5(module: String) = "org.junit.jupiter:junit-jupiter-$module:5.0.0"

    apply {
        plugin("kotlin")
        plugin("jacoco")
        plugin("maven-publish")
        plugin("signing")
        plugin("org.jetbrains.dokka")
        plugin("com.adarshr.test-logger")
        plugin("org.jmailen.kotlinter")
    }

    group = "org.valiktor"

    repositories {
        mavenCentral()
    }

    dependencies {
        compile(kotlin("stdlib"))

        testCompile(kotlin("test-junit5"))
        testCompile(assertj("core"))
        testRuntime(junit5("engine"))
    }

    testlogger {
        setTheme("mocha")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.6"
            }
        }

        withType<Test> {
            useJUnitPlatform()

            // fix for JDK > 8 (see http://openjdk.java.net/jeps/252)
            systemProperty("java.locale.providers", "JRE,SPI")
        }

        withType<DokkaTask> {
            outputFormat = "javadoc"
            outputDirectory = "$buildDir/javadoc"
            inputs.dir("src/main/kotlin")
        }

        withType<JacocoReport> {
            reports {
                xml.isEnabled = true
                html.isEnabled = true
            }

            val jacocoTestCoverageVerification by tasks
            jacocoTestCoverageVerification.dependsOn(this)
        }

        withType<JacocoCoverageVerification> {
            violationRules {
                rule {
                    limit {
                        minimum = BigDecimal.valueOf(0.3)
                    }
                }
            }
            val check by tasks
            check.dependsOn(this)
        }
    }

    publishing {
        val ossrhUsername: String by project
        val ossrhPassword: String by project

        repositories {
            maven(url = "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
        (publications) {
            "mavenJava"(MavenPublication::class) {
                val binaryJar = components["java"]

                val sourcesJar by tasks.creating(Jar::class) {
                    classifier = "sources"
                    from(java.sourceSets["main"].allSource)
                }

                val javadocJar by tasks.creating(Jar::class) {
                    classifier = "javadoc"
                    from("$buildDir/javadoc")
                }

                from(binaryJar)
                artifact(sourcesJar)
                artifact(javadocJar)

                pom {
                    name.set("Valiktor")
                    description.set("Valiktor is a type-safe, powerful and extensible fluent DSL to validate objects written in Kotlin.")
                    url.set("https://www.valiktor.org")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("rodolphocouto")
                            name.set("Rodolpho Sbaraglini Couto")
                            email.set("rodolpho.sbaraglini@gmail.com")
                        }
                    }
                    scm {
                        url.set("https://www.github.com/valiktor/valiktor")
                        connection.set("scm:git:https://www.github.com/valiktor/valiktor")
                        developerConnection.set("scm:git:https://www.github.com/rodolphocouto")
                    }
                }
            }
        }
    }

    afterEvaluate {
        signing {
            sign(publishing.publications["mavenJava"])
        }
    }
}