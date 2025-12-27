plugins {
    kotlin("jvm") version "2.2.21"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("com.vanniktech.maven.publish") version "0.33.0"
}

group = "io.github.timvanoijen.kotlin"
version = file("version").readText().trim()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "krational", version.toString())

    pom {
        name.set("Kotlin Rational")
        description.set("A Kotlin lib for rational numbers.")
        inceptionYear.set("2025")
        url.set("https://github.com/timvanoijen/krational/")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("timvanoijen")
                name.set("Tim van Oijen")
                url.set("https://github.com/timvanoijen/")
            }
        }
        scm {
            url.set("https://github.com/timvanoijen/krational/")
            connection.set("scm:git:git://github.com/timvanoijen/krational.git")
            developerConnection.set("scm:git:ssh://git@github.com/timvanoijen/krational.git")
        }
    }
}

tasks.register("incrementMinorVersion") {
    doLast {
        val versionFile = file("version")
        val currentVersion = versionFile.readText().trim()
        val versionParts = currentVersion.split(".")
        val major = versionParts[0]
        val minor = versionParts[1].toInt()
        val newVersion = "$major.${minor + 1}.0-SNAPSHOT"
        versionFile.writeText(newVersion)
    }
}
