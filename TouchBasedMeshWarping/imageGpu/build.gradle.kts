plugins {
    alias(libs.plugins.android.library)
}

/** CMake version when [usingCMakeCompile] is on: `cge.cmakeVersion`, or `rootProject.ext.android`, else 3.22.1 */
fun cmakeVersionFromProject(): String {
    val fromProp = findProperty("cge.cmakeVersion") ?: rootProject.findProperty("cge.cmakeVersion")
    if (fromProp != null) return fromProp.toString()
    val ext = rootProject.extensions.extraProperties
    if (ext.has("android")) {
        val android = ext.get("android")
        if (android is Map<*, *>) {
            android["cmakeVersion"]?.let { return it.toString() }
        }
    }
    return "3.22.1"
}

private fun gradleExtFlag(name: String): Boolean {
    val ext = gradle.extensions.extraProperties
    if (!ext.has(name)) return false
    return when (val v = ext.get(name)) {
        is Boolean -> v
        else -> false
    }
}

fun usingCMakeCompile() = gradleExtFlag("usingCMakeCompile")

fun usingCMakeCompileDebug() = gradleExtFlag("usingCMakeCompileDebug")

fun disableVideoModule() = gradleExtFlag("disableVideoModule")

fun deployArtifacts() = gradleExtFlag("deployArtifacts")

/** Clears JNI source dirs (same as Groovy `jni.srcDirs = []`); Kotlin's `SourceSet` receiver hides `getJni()`. */
private fun clearAndroidJniSourceDirs(sourceSet: Any) {
    val jni = sourceSet.javaClass.getMethod("getJni").invoke(sourceSet)
    jni.javaClass.getMethod("setSrcDirs", Iterable::class.java).invoke(jni, emptyList<Any>())
}

android {
    namespace = "org.wysaid.library"
    compileSdk = 36

    if (usingCMakeCompile()) {
        ndkVersion = "23.1.7779620"
    }

    defaultConfig {
        minSdk = 24

        buildConfigField("boolean", "CGE_USE_VIDEO_MODULE", if (disableVideoModule()) "false" else "true")

        if (usingCMakeCompile()) {
            externalNativeBuild {
                cmake {
                    val cmakeBuildType: String
                    val cppExtraFlags: String
                    if (usingCMakeCompileDebug()) {
                        cmakeBuildType = "-DCMAKE_BUILD_TYPE=Debug"
                        cppExtraFlags = "-DDEBUG=1 -D_DEBUG=1 -Od -g"
                    } else {
                        cmakeBuildType = "-DCMAKE_BUILD_TYPE=Release"
                        cppExtraFlags = "-Os -DNDEBUG=1 -D_NDEBUG=1"
                    }

                    arguments(
                        "-DANDROID_STL=c++_static",
                        "-DANDROID_ARM_NEON=TRUE",
                        "-DANDROID_GRADLE=ON",
                        cmakeBuildType,
                        "-DCGE_USE_VIDEO_MODULE=OFF",
                    )

                    cFlags(
                        "-ffast-math -fPIE -fPIC -DNO_LOCALE_SUPPORT=1 -DANDROID_NDK=1 -D__STDC_CONSTANT_MACROS=1 $cppExtraFlags",
                    )
                    cppFlags(
                        "-ffast-math -fPIE -fPIC -DNO_LOCALE_SUPPORT=1 -DANDROID_NDK=1 -D__STDC_CONSTANT_MACROS=1 -frtti -std=c++14 -fno-exceptions -fvisibility-inlines-hidden $cppExtraFlags",
                    )

                    logger.lifecycle("cmake externalNativeBuild configured (build type: $cmakeBuildType)")
                    logger.lifecycle("cppFlags: $cppExtraFlags")
                }
            }

            ndk {
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
            }
        }
    }

    if (usingCMakeCompile()) {
        externalNativeBuild {
            cmake {
                path = file("src/main/jni/CMakeLists.txt")
                version = cmakeVersionFromProject()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            if (!usingCMakeCompile()) {
                @Suppress("DEPRECATION")
                jniLibs.srcDir("src/main/libs")
                clearAndroidJniSourceDirs(this)
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
}