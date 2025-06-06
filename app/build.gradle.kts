plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.dv_estoque"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dv_estoque"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Apache POI para manipulação de Excel (versão light)
    implementation ("org.apache.poi:poi-ooxml:5.2.3")
    implementation ("com.fasterxml.jackson.core:jackson-core:2.14.2")

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.android.material:material:1.10.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}