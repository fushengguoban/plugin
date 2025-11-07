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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MoreWX"
include(":app")
//
include(":SecondModule")
//include (":Maven-Wgllss-Dynamic-Plugin-Library")
//include (":Maven-Wgllss-Dynamic-Plugin-RunTime-Apk")
//
//
//
//
//
//project(":Maven-Wgllss-Dynamic-Plugin-Library").projectDir = file("Maven-Wgllss-Dynamic-Plugin-SDK/Maven-Wgllss-Dynamic-Plugin-Library")
//project(":Maven-Wgllss-Dynamic-Plugin-RunTime-Apk").projectDir = file("Maven-Wgllss-Dynamic-Plugin-SDK/Maven-Wgllss-Dynamic-Plugin-RunTime-Apk")


include(":common")
