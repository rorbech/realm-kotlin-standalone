pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        // Only required for realm-kotlin snapshots
        maven("https://oss.sonatype.org/content/repositories/snapshots")
//        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // Only required for realm-kotlin snapshots
        maven("https://oss.sonatype.org/content/repositories/snapshots")
//        mavenLocal()
    }
}

rootProject.name = "java-console-sample"


include(":jvm")

