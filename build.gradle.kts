plugins {
    java
    jacoco
    `maven-publish`
}

group = "net.exoego.aseprite4j"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")

    group = "net.exoego.aseprite4j"
    version = "1.0-SNAPSHOT"

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

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
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
