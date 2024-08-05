import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlinx.serializtion)
    alias(libs.plugins.ktlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.firebase.crashlytics)

}

val keystoreProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "keystore.properties")))
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    compileSdk = 34
    defaultConfig {
        applicationId = "com.jk.mr.duo.clock"
        minSdk = 26
        targetSdk = 34
        versionCode = 31
        versionName = "2.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        val mapboxAccessToken: String by project
        val bingApiKey: String by project
        val microsoftTimeZoneBaseURL: String by project
        val openStreetMap: String by project
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", mapboxAccessToken)
        buildConfigField("String", "BING_MAP_KEY", bingApiKey)
        buildConfigField("String", "MICROSOFT_TIMEZONE_BASE_URL", microsoftTimeZoneBaseURL)
        buildConfigField("String", "OPENSTREETMAP", openStreetMap)
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            //   buildConfigField 'String', 'GoogleSecAPIKEY', mapkey

            signingConfig = signingConfigs.getByName("debug")
        }
    }
    namespace = "com.jk.mr.duo.clock"
}

dependencies {
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)

    // Ktor Client
    implementation(libs.ktor.clientloggging)
    implementation(libs.ktor.content.negotioation)
    implementation(libs.ktor.kotlinx.json)
    //implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.auth)

    //Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    //implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    implementation(libs.coil)
    implementation(libs.coil.svg)

    //implementation(libs.mapbox.search.android.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    /* configurations{
         all*.exclude group: 'com.google.guava', module: 'listenablefuture'
     }*/
}

/*apply plugin: 'com.google.gms.google-services'
apply plugin: "org.jlleitschuh.gradle.ktlint"*/
