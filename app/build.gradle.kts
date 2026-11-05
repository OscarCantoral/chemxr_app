plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.example.ch3mxr"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.ch3mxr"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // La instrumentacion la maneja Kover (koverXmlReport); la JaCoCo de AGP queda fuera
            // para evitar que dos agentes instrumenten los mismos tests unitarios.
            enableUnitTestCoverage = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

// Kover: cobertura para SonarCloud (sonar.kotlin.coverage.reportPaths apunta al XML total)
kover {
    reports {
        filters {
            excludes {
                // Igual criterio que sonar.coverage.exclusions: clases generadas
                classes(
                    "*_Hilt*",
                    "*Hilt_*",
                    "*_Factory*",
                    "*_MembersInjector*",
                    "*_Provide*",
                    "*_FactoryProvider*",
                    "hilt_*",
                )
                // Recursos generados por AGP
                classes("*R", "*.R$*", "*Manifest*")
                // Clases anotadas por Hilt (inicializacion generada, no hay logica de negocio)
                annotatedBy(
                    "dagger.hilt.android.HiltAndroidApp",
                    "dagger.hilt.android.AndroidEntryPoint",
                    "dagger.hilt.android.lifecycle.HiltViewModel",
                )
            }
        }
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation("com.google.ar:core:1.56.0")
    implementation("io.github.sceneview:arsceneview:4.35.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.android.pdf.viewer)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.facebook.login)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}