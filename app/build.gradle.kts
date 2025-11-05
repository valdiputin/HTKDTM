import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}


android {
    namespace = "vn.edu.tlu.cse.ht1.lequocthinh.kdtm"
    compileSdk = 36

    defaultConfig {
        // 🔥 CÁCH ĐƠN GIẢN: Đọc trực tiếp từ gradle.properties hoặc local.properties
        val apiKey = providers.gradleProperty("GEMINI_API_KEY").orNull
            ?: project.findProperty("GEMINI_API_KEY")?.toString()
            ?: ""

        println("🔑 GEMINI_API_KEY loaded: ${if (apiKey.isNotEmpty()) "YES (${apiKey.length} chars)" else "NO - EMPTY!"}")

        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")

        applicationId = "vn.edu.tlu.cse.ht1.lequocthinh.kdtm"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Google Gemini AI (Chỉ giữ lại phiên bản mới nhất: 0.9.0)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)

    // 🔥 THÊM: Google Play Services Auth (BẮT BUỘC cho Google Sign-In)
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // UI Components
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.glide)

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        allWarningsAsErrors = false
    }
}