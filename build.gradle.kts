plugins {
    java
    jacoco
    `maven-publish`
    signing
}

group = "net.exoego.aseprite4j"
version = System.getenv("RELEASE_VERSION") ?: "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    group = "net.exoego.aseprite4j"
    version = rootProject.version

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:6.0.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testImplementation("com.google.truth:truth:1.4.5")
    }
}

project(":core") {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    java {
        withJavadocJar()
        withSourcesJar()
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "aseprite4j-core"
                from(components["java"])

                pom {
                    name.set("aseprite4j-core")
                    description.set("A Java library for reading Aseprite files")
                    url.set("https://github.com/exoego/aseprite4j")

                    licenses {
                        license {
                            name.set("Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("exoego")
                            name.set("TATSUNO Yasuhiro")
                            email.set("ytatsuno.jp@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/exoego/aseprite4j.git")
                        developerConnection.set("scm:git:ssh://github.com:exoego/aseprite4j.git")
                        url.set("https://github.com/exoego/aseprite4j")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
                url = if (version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://oss.sonatype.org/content/repositories/snapshots/")
                } else {
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                }
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }

    configure<SigningExtension> {
        val signingKey = System.getenv("GPG_PRIVATE_KEY")
        val signingPassword = System.getenv("GPG_PASSPHRASE")
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(the<PublishingExtension>().publications["maven"])
        }
    }
}

allprojects {
    tasks.jacocoTestReport {
        reports {
            xml.required = true
            csv.required = false
        }
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }
}
