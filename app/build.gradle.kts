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
    }

    lint {
        abortOnError = false
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