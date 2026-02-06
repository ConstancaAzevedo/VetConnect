plugins {
    id("com.android.application")
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "pt.ipt.dam2025.vetconnect"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "pt.ipt.dam2025.vetconnect"
        minSdk = 28
        targetSdk = 36
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

    //chave que vai ativar o binding
    // o binding vai criar uma ligação direta entre o kotlin e o xml
    //copiado do outro ficheiro "code-Camera X app.vf"
    // allow the access to objects of the interface, from code,
    // in 'binding'
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    ksp("com.github.bumptech.glide:ksp:5.0.5")

    // Retrofit & Gson -> para a API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.logging.interceptor)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // código do professor para Camera
    //copiado do outro ficheiro "code-Camera X app.vf"
    // CameraX core library using the camera2 implementation
    val cameraxVersion = "1.5.1"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    // implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation(libs.androidx.camera.core)
    // implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    // implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    //    implementation "androidx.camera:camera-video:${camerax_version}"
    // If you want to additionally use the CameraX View class
    // implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    //       implementation "androidx.camera:camera-mlkit-vision:${camerax_version}"
    // If you want to additionally use the CameraX Extensions library
    // implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    implementation(libs.androidx.camera.extensions)

}