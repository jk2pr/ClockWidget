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
        val SDK_TOKEN: String by settings
        maven{
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = SDK_TOKEN
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

include ("app")