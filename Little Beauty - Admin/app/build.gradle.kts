plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.flatcode.beautytouchadmin"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.flatcode.beautytouchadmin"
        minSdk = 24
        targetSdk = 37
        versionCode = 5
        versionName = "1.30"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    //Layout
    implementation(libs.material)
    implementation(libs.multicolors)
    //Image
    implementation(libs.coil3)                          //Coil Image
    implementation(libs.coil3.network.okhttp)
    api(libs.android.image.cropper)                     //Image Crop
    //MVVM
    implementation(libs.androidx.lifecycle.viewmodel)
    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    //Coroutines
    implementation(libs.kotlinx.coroutines.android)
    //Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    //Firebase
    implementation(platform(libs.firebase.bom)) //Firebase BOM
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    //Cloudinary
    implementation(libs.cloudinary.android)
    //Other
    implementation(libs.material.ripple)                //Ripple Effect
    implementation(libs.timber)
}