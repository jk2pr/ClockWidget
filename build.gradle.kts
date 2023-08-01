// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(AppPath.gradle)
        classpath(AppPath.kotlinPlugin)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        classpath("com.google.dagger:hilt-android-gradle-plugin:2.43.2")

        classpath("com.google.gms:google-services:4.3.15") // Google Services plugin
        classpath(AppPath.serializtionClassPath)
    }
}
plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
}
allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks.register("type", Delete::class) {
    delete(rootProject.buildDir)
}
