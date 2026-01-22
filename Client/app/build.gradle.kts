plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.clientchodientu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.clientchodientu"
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
}

dependencies {
    // --- PHẦN GỐC CỦA FILE 1 ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- THÊM MỚI TỪ FILE 2 ---
    // Thay dòng bị lỗi bằng dòng này:
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0") // Thêm cái này (Zoom ảnh)

    // --- TIẾP TỤC PHẦN GỐC CỦA FILE 1 ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Networking (Giữ bản 4.12.0 của File 1 vì mới hơn bản 4.10.0 của File 2)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // UI & Media
    implementation("com.google.android.material:material:1.12.0") // Giữ bản 1.12.0 mới hơn
    implementation("com.github.bumptech.glide:glide:5.0.5")

    // Firebase (File 1 đầy đủ hơn nên giữ nguyên)
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")
    // implementation("com.google.firebase:firebase-auth-ktx") // Đang comment thì giữ nguyên comment
    // implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-database")
}