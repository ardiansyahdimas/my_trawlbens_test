plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}
android {
    namespace = "com.mytrawlbenstest"
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        applicationId = "com.mytrawlbenstest"
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        viewBinding =  true
    }
}

dependencies {
    implementation (project(":core"))
    implementation(Deps.core)
    implementation(Deps.appCompat)
    implementation(Deps.materialDesign)
    implementation(Deps.constraintLayout)
    testImplementation(Deps.junit)

    implementation (Deps.daggerHilt)
    kapt (Deps.daggerHiltCompiler)
    implementation (Deps.activity)
    implementation (Deps.fragment)

    implementation (Deps.roomRuntime)
    kapt (Deps.roomCompiler)
    implementation (Deps.coroutinesCore)
    implementation (Deps.coroutines)
    implementation (Deps.room)
    api (Deps.lifeData)
    implementation (Deps.timber)

    implementation (Deps.recyclerview)
    implementation (Deps.glide)
    kapt (Deps.glideCompiler)
    implementation (Deps.circleimageview)

    implementation (Deps.retrofit)
    implementation (Deps.retrofitGson)
    implementation (Deps.retrofitMoshi)
    implementation (Deps.retrofitRxJava)
    implementation (Deps.okhttp3Logging)
    implementation (Deps.okhttp3)
    implementation (Deps.sparrow007)

    implementation (Deps.sqlCipher)
    implementation (Deps.sqlite)
    implementation (Deps.cookieBar)
    implementation (Deps.lottie)
}