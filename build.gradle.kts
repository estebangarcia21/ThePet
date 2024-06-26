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

    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
//    maven {
//        name = "citizens-repo"
//        url = uri("https://maven.citizensnpcs.co/repo")
//    }
    maven("https://jitpack.io/")
}

dependencies {
    testImplementation(kotlin("test"))
//    implementation("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    implementation("org.spigotmc:spigot:1.20.6-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
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

    from(layout.buildDirectory.file("libs/$out"))
    into("server/plugins")
}
