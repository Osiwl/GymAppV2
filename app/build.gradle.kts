plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.gymappv2"

    // Aquí actualizamos a la versión 37
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.gymappv2"
        minSdk = 24

        // Aquí también actualizamos a 37 para que coincida
        targetSdk = 37
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    // =====================================================
    // CREDENTIAL MANAGER + GOOGLE SIGN-IN
    // =====================================================

    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")


    // =====================================================
    // JETPACK COMPOSE
    // =====================================================

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(
        "androidx.compose.material:material-icons-extended"
    )


    // =====================================================
    // FIREBASE
    // =====================================================

    implementation(libs.firebase.auth)

    implementation(libs.firebase.firestore)


    // =====================================================
    // COIL - IMÁGENES / GIF
    // =====================================================

    implementation(
        "io.coil-kt:coil-compose:2.6.0"
    )

    implementation(
        "io.coil-kt:coil-gif:2.6.0"
    )


    // =====================================================
    // TESTS
    // =====================================================

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}