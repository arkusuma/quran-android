import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.grafian.quran"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.grafian.quran"
        minSdk = 21
        targetSdk = 34
        versionCode = 22
        versionName = "1.8.0"
        externalNativeBuild {
            ndkBuild {
                arguments += "NDK_DEBUG=1"
            }
        }

    }

    lint {
        abortOnError = false
    }

    signingConfigs {
        create("release") {
            val prop = Properties().apply {
                load(File("signing.properties").reader())
            }
            storeFile = File(prop.getProperty("storeFile"))
            storePassword = prop.getProperty("storePassword")
            keyAlias = prop.getProperty("keyAlias")
            keyPassword = prop.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    ndkVersion = "27.2.12479018"
    buildToolsVersion = "35.0.0"
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/cpp/Android.mk")
        }
    }
}

dependencies {

    implementation(libs.gson)
    implementation(libs.material)
}