import org.gradle.kotlin.dsl.testRuntimeOnly

plugins {
    java
}

group = "net.exoego"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testImplementation("com.google.truth:truth:1.4.4")
    }
}

allprojects {
    tasks.test {
        useJUnitPlatform()
    }
}
