plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ivantrykosh.udemy_course.android14.projemanag"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ivantrykosh.udemy_course.android14.projemanag"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.firebase:firebase-auth:19.2.0")
    implementation("com.google.firebase:firebase-firestore:21.4.0")
    implementation("com.google.firebase:firebase-storage:19.1.1")
    implementation("com.google.firebase:firebase-messaging:23.0.6")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    val glide_version = "4.11.0"
    implementation("com.github.bumptech.glide:glide:$glide_version")
    annotationProcessor("com.github.bumptech.glide:compiler:$glide_version")

    val coroutines_version = "1.5.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines_version")

    val dagger_hilt_version = "2.48"
    implementation("com.google.dagger:hilt-android:$dagger_hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$dagger_hilt_version")

    val lifecycle_version = "2.8.4"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    testImplementation("org.mockito:mockito-core:3.4.0")
    testImplementation("org.mockito:mockito-inline:4.5.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC.2")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
}

kapt {
    correctErrorTypes = true
}