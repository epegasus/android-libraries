plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.sohaib.imagefilters"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sohaib.imagefilters"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        jniLibs {
            // Work around Android 14 RELRO/OOM crashes on some devices by forcing extraction.
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // wysaid
    implementation(libs.gpuimage)
}