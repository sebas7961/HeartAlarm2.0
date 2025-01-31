plugins {
    alias(libs.plugins.android.application)
    id ("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.heartalarm20"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.heartalarm20"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {
    // Firebase libraries con versiones compatibles
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.android.gms:play-services-auth:20.7.0")


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

    //para servidor firebaseMessaging
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    //para adorid timeworker creo que es demasiado util
    implementation("androidx.work:work-runtime:2.10.0")
    implementation("com.google.guava:guava:31.0.1-android")

    //auth de google, aquí está renatin
    implementation("com.google.auth:google-auth-library-oauth2-http:1.13.0")

    //Chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.0")
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
