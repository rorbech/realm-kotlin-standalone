//buildscript {
//    repositories {
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
//    }
//    dependencies {
//        classpath("io.realm.kotlin:gradle-plugin:1.12.0-NEXTMAJORCORE-SNAPSHOT")
//    }
//}
plugins {
    kotlin("jvm")
    application
    id("io.realm.kotlin")
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.realm.kotlin:library-base:${rootProject.extra["realmVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}
