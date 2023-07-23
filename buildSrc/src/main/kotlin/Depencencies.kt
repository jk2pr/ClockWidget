object AppPath {
    object Versions {
        const val gradle_version = "8.0.2"
        const val kotlin_version = "1.8.22"
        const val support_version = "27.1.1"
        const val klint_version = "10.2.0"
        const val kotlin_serialization_version = "1.8.10"
    }

    val gradle = "com.android.tools.build:gradle:${Versions.gradle_version}"
    val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_version}"
    val serializtionClassPath =
        "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin_serialization_version}"

}


//Now Open App Leavel build.gradle.kts for modification

//Frist replace plugin part
object PluginsId {
    const val androidApplication = "com.android.application"

    //Adding Android Library Plugin Id
    const val android_library = "com.android.library"
    const val kotlin_android = "kotlin-android"
    const val kotlin_extension = "kotlin-android-extensions"
    const val junit5 = "de.mannodermaus.android-junit5"

}

//Repace android Parts
object App {
    const val compileSdk = 29
    const val buildToolVersion = "29.0.2"
    const val applicationId = "com.sippitechnologies.ktsforandroid"
    const val minSdkVersion = 15
    const val targetSdkVersion = 29
    const val versionCode = 1
    const val versionName = "1.0"
    const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
}

//Replace Depencies block
object Libraries {
    object Versions {

        const val hilt_version = "2.43.2"
        const val glance_version = "1.0.0-alpha03"
        const val compose_version = "1.4.0"
        const val appcompat_version = "1.6.1"
        const val ktx_version = "2.5.3"
        const val gson_converter_version = "2.9.0"
        const val ktor_version = "2.2.4"
        const val kotlinx_serialization_version = "1.4.1"

    }

    object Lib {
        const val hilt = "com.google.dagger:hilt-android:${Versions.hilt_version}"
        const val glance = "androidx.glance:glance-appwidget:${Versions.glance_version}"
        const val hilt_compiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"
        const val hilt_lint_aar = "com.google.dagger:dagger-lint-aar:${Versions.hilt_version}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat_version}"
        const val fragmentKtx =
            "androidx.navigation:navigation-fragment-ktx:${Versions.ktx_version}"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.ktx_version}"
        const val retrofitGsonConvertor =
            "com.squareup.retrofit2:converter-gson:${Versions.gson_converter_version}"

        //KTOR

        const val ktorClientAndroid = "io.ktor:ktor-client-android:${Versions.ktor_version}"
        const val ktorContentNegotioation =
            "io.ktor:ktor-client-content-negotiation:${Versions.ktor_version}"
        const val ktorKotlinxJson =
            "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor_version}"
        const val kotlinxSerialization =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinx_serialization_version}"
        const val ktorClientLoggging = "io.ktor:ktor-client-logging-jvm:${Versions.ktor_version}"


    }

}

object BuildPlugins {


    const val kotlinSerializationPlugin = "kotlinx-serialization"
    const val hiltPlugin = "dagger.hilt.android.plugin"
    const val androidApplicationPlugin = "com.android.application"
    const val kotlinAndroidPlugin = "kotlin-android"
    const val kotlinKaptPlugin = "kotlin-kapt"
    const val ktlintPlugin = "org.jlleitschuh.gradle.ktlint"

}
