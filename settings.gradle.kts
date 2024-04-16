pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "MVIModularizationTemplate"
include(":app")
include(":core:designSystem")
include(":core:navigation")
include(":common:exceptions")
include(":features:splash")
include(":features:main")
include(":features:onboarding")
include(":features:app")



