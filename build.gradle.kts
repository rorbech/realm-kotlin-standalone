buildscript {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath("io.realm.kotlin:gradle-plugin:1.12.0-NEXTMAJORCORE-SNAPSHOT")
    }
}

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0" apply false
    application
}

// Explicitly adding the plugin to the classpath as it makes it easier to control the version
// centrally (don't need version in the 'plugins' block). Further, snapshots are not published with
// marker interface so would need to be added to the classpath manually anyway.
rootProject.extra["realmVersion"] = "1.12.0-NEXTMAJORCORE-SNAPSHOT"

group = "org.example"
version = "1.0-SNAPSHOT"
