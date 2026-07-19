plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.nikhil.habit_money"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.nikhil.habit_money"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL",
                "\"${project.findProperty("base.url") ?: "http://10.0.2.2:8080/"}\"")
            buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID",
                "\"${project.findProperty("google.server.client.id") ?: "66148283642-68qepdsnidmuj1lfbgk8odpc0ff6jmre.apps.googleusercontent.com"}\"")
        }
        release {
            buildConfigField("String", "BASE_URL",
                "\"${project.findProperty("base.url") ?: "http://10.0.2.2:8080/"}\"")
            buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID",
                "\"${project.findProperty("google.server.client.id") ?: "66148283642-68qepdsnidmuj1lfbgk8odpc0ff6jmre.apps.googleusercontent.com"}\"")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.viewmodel)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
}