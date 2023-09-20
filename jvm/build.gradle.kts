plugins {
    kotlin("jvm")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.realm.kotlin:library-base:${rootProject.extra["realmVersion"]}")
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
