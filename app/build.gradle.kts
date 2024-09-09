import com.android.build.api.variant.BuildConfigField

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.ssau_schedule"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ssau_schedule"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    androidComponents {
        onVariants {
            it.buildConfigFields.putAll(mapOf(
                Pair("BASE_URL",
                    BuildConfigField("String",
                        "\"https://lk.ssau.ru/\"", null)),
                Pair("SIGN_IN_URL",
                    BuildConfigField("String",
                        "\"account/login\"", null)),
                Pair("USER_DETAILS_URL",
                    BuildConfigField("String",
                        "\"api/proxy/current-user-details\"", null)),
                Pair("USER_GROUPS_URL",
                    BuildConfigField("String",
                        "\"api/proxy/personal/groups\"", null)),
                Pair("YEARS_URL",
                    BuildConfigField("String",
                        "\"api/proxy/dictionaries?slug=unified_years\"", null))
            ))
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.squareup.okhttp)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)
}