import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
}

val workingDirPath: String by extra {
    val inputStream = project.rootProject.file("local.properties").inputStream()
    val properties = Properties()
    properties.load(inputStream)
    inputStream.close()
    properties.getProperty("workingDirPath")
}