plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.heartalarm20"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.heartalarm20"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Firebase libraries con versiones compatibles
    implementation ("com.google.firebase:firebase-firestore:24.6.0")
    implementation ("com.google.firebase:firebase-database:20.2.1")
    implementation ("com.google.firebase:firebase-messaging:23.2.1")

    // Otras dependencias
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.databinding:databinding-runtime:7.0.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("androidx.core:core-ktx:1.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
