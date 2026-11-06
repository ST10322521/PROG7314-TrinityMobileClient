import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    // Room setup
    alias(libs.plugins.ksp)
}

val localProps = Properties().also { props ->
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
}
val hubUrl: String = localProps.getProperty("HUB_URL") ?: System.getenv("HUB_URL") ?: ""

android {
    namespace = "Emeris.PROG7314.trinitymobileclient"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "Emeris.PROG7314.trinitymobileclient"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "HUB_URL", "\"$hubUrl\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Auth dependency
    implementation("com.google.firebase:firebase-auth")
    // Google SSO
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // Room
    implementation(libs.androidx.room3.runtime)
    implementation(libs.androidx.room3.ktx)
    ksp(libs.androidx.room3.compiler)

    // Gson
    implementation(libs.gson)

    // SignalR
    implementation(libs.signalr)

    // WorkManager
    implementation(libs.work.runtime.ktx)
    
    // Biometrics
    implementation("androidx.biometric:biometric:1.1.0")
}
