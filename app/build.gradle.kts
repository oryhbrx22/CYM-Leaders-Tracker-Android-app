plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace="app.cym.tracker"
    compileSdk=35
    defaultConfig {
        applicationId="app.cym.tracker"
        minSdk=23
        targetSdk=35
        versionCode=1
        versionName="1.0"
    }
    buildTypes {
        debug {
            applicationIdSuffix=".debug"
            versionNameSuffix="-debug"
            buildConfigField("String","APP_NAME","\"CYM Leaders Tracker\"")
        }
    }
    buildFeatures { buildConfig=true }
}
dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
}
