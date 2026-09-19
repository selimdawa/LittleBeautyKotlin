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
    implementation(libs.androidx.datastore.preferences)   //DataStore
    //Layout
    implementation(libs.material)
    implementation(libs.multicolors)
    //Image
    implementation(libs.coil3)                          //Coil Image
    implementation(libs.coil3.network.okhttp)
    api(libs.android.image.cropper)                     //Image Crop
    //MVVM & Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    //Coroutines
    implementation(libs.kotlinx.coroutines.android)
    //Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)
    //Firebase
    implementation(platform(libs.firebase.bom)) //Firebase BOM
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.cloudinary.android)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    //Other's
    implementation(libs.material.ripple)                //Ripple Effect
    implementation(libs.timber)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}