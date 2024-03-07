plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidxNavigation)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.filmsmatch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.filmsmatch"
        minSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    // Зависимость для интеграции Hilt в приложение Android
    implementation(libs.hilt.android)
    // Плагин kapt для обработки аннотаций Hilt во время компиляции
    kapt(libs.hilt.android.compiler)
    // Зависимость для скелетонов
    implementation(libs.shimmer)

    implementation(libs.picasso)

    implementation(libs.viewpager)
    // Зависимость для использования Kotlin Extensions в AndroidX
    implementation(libs.androidx.core.ktx)
    // Зависимость для использования AppCompatActivity из AndroidX
    implementation(libs.androidx.appcompat)
    // Зависимость для использования Material Design компонентов
    implementation(libs.material)
    // Зависимость для использования Activity из AndroidX
    implementation(libs.androidx.activity)
    // Зависимость для использования ConstraintLayout из AndroidX
    implementation(libs.androidx.constraintlayout)
    // Зависимость для использования пользовательского интерфейса навигации из AndroidX
    implementation(libs.androidx.navigation.ui)
    // Зависимость для использования фрагментов навигации из AndroidX
    implementation(libs.androidx.navigation.fragment)
    // Зависимость для unit-тестирования с использованием JUnit
    testImplementation(libs.junit)
    // Зависимость для инструментального тестирования с использованием AndroidX JUnit
    androidTestImplementation(libs.androidx.junit)
    // Зависимость для инструментального тестирования с использованием Espresso
    androidTestImplementation(libs.androidx.espresso.core)
}
