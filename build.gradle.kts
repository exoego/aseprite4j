import org.gradle.internal.impldep.org.jsoup.nodes.Document
import org.gradle.kotlin.dsl.testRuntimeOnly

plugins {
    java
    jacoco
}

group = "net.exoego"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:6.0.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testImplementation("com.google.truth:truth:1.4.5")
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
