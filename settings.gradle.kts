pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("retrofit2-retrofit", "com.squareup.retrofit2", "retrofit").version("2.9.0")
            library("androidx-lifecycle-viewmodel-compose", "androidx.lifecycle", "lifecycle-viewmodel-compose").version("2.7.0")
            library("retrofit2-converter-gson", "com.squareup.retrofit2", "converter-gson").version("2.9.0")
            library("coil-kt-coil-compose", "io.coil-kt", "coil-compose").version("2.5.0")
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PMU"
include(":app")
 