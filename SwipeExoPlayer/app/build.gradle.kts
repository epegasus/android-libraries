plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sohaib.swipeexoplayer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sohaib.swipeexoplayer"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Fragment ktx
    implementation(libs.androidx.fragment.ktx)

    // LifeCycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
}