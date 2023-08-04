pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        val mapBoxDownloadToken: String by settings
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = mapBoxDownloadToken
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

include("app")
