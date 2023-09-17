import java.io.FileInputStream
import java.util.Properties

plugins {
    id(BuildPlugins.kotlinSerializationPlugin)
    id(BuildPlugins.hiltPlugin)
    id(BuildPlugins.androidApplicationPlugin)
    id(BuildPlugins.kotlinAndroidPlugin)
    id(BuildPlugins.kotlinKaptPlugin)
    id(BuildPlugins.ktlintPlugin)
    id(BuildPlugins.firebasePerformancePlugin)
    id(BuildPlugins.googleServicesPlugin)
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
        minSdk = 21
        targetSdk = 34
        versionCode = 31
        versionName = "2.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        val mapboxAccessToken: String by project
        val bingApiKey: String by project
        val microsoftTimeZoneBaseURL: String by project
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", mapboxAccessToken)
        buildConfigField("String", "BING_MAP_KEY", bingApiKey)
        buildConfigField("String", "MICROSOFT_TIMEZONE_BASE_URL", microsoftTimeZoneBaseURL)
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    /* flavorDimensions "mode"

    productFlavors {
        free {
            applicationIdSuffix ".free"
            dimension "mode"
        }
        paid {
            applicationIdSuffix ".paid"
            dimension "mode"
        }
    }*/

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

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // hilt
    implementation(Libraries.Lib.hilt)
    implementation(Libraries.Lib.glance)
    implementation(Libraries.Lib.hilt_lint_aar)
    kapt(Libraries.Lib.hilt_compiler)

    implementation(Libraries.Lib.appcompat)
    implementation(Libraries.Lib.fragmentKtx)
    implementation(Libraries.Lib.navigationUiKtx)
    //   implementation 'androidx.legacy:legacy-support-v4:1.0.0'

    // Json Converter
    implementation(Libraries.Lib.retrofitGsonConvertor)

    // Ktor Client
    implementation(Libraries.Lib.ktorClientAndroid)
    implementation(Libraries.Lib.ktorContentNegotioation)
    implementation(Libraries.Lib.ktorKotlinxJson)
    implementation(Libraries.Lib.kotlinxSerialization)
    implementation(Libraries.Lib.ktorClientLoggging)

    // implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-places-v7:0.7.0'
    // implementation "com.mapbox.search:autofill:1.0.0-beta.45"
    /* implementation('com.mapbox.mapboxsdk:mapbox-android-sdk:7.0.0@aar') {
         transitive = true
     }*/
    implementation("com.mapbox.search:mapbox-search-android-ui:1.0.0-rc.3")

    implementation("androidx.compose.ui:ui-viewbinding:1.5.1")
    // implementation 'androidx.appcompat:appcompat:1.1.0-alpha02'
    //  implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    // implementation 'androidx.vectordrawable:vectordrawable:1.1.0-alpha01'
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("app.cash.turbine:turbine:0.12.3")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0")

    // SVG
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-svg:2.2.1")

    // (Recommended) Add Analytics

    // compose
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.navigation:navigation-compose:2.7.2")
    // Preview
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Firebase  Performance metric
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-perf-ktx")

    // Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    /* configurations{
         all*.exclude group: 'com.google.guava', module: 'listenablefuture'
     }*/
}

/*apply plugin: 'com.google.gms.google-services'
apply plugin: "org.jlleitschuh.gradle.ktlint"*/
