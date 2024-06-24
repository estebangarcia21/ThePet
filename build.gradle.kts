import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "org.bluehats"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

project.setProperty("mainClassName", "org.bluehats.ThePet")

tasks.named<ShadowJar>("shadowJar") {
    val property = findProperty("archiveFileName") as String?
    archiveFileName.set(property)
}

tasks.register<Copy>("localBuild") {
    dependsOn("shadowJar")

    val out = findProperty("archiveFileName") as String?
    print(out)

    from(layout.buildDirectory.file("libs/$out"))
    into("server/plugins")
}
