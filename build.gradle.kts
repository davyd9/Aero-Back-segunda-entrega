plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
}

allprojects {
    if (rootDir.absolutePath.contains("OneDrive", ignoreCase = true)) {
        layout.buildDirectory.set(file("${System.getProperty("java.io.tmpdir")}/AeroBackBuild/${project.name}"))
    }
}
