plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.moutamid.chama"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moutamid.chama"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "Chama-$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { viewBinding = true }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.stash)
    implementation(libs.materialdrawer)
    implementation(libs.mpandroidchart)
    implementation(libs.glide)
    implementation(libs.imagepicker)
    implementation(libs.signseekbar)
    implementation(libs.circle.imageview)
    implementation(libs.hbb20.ccp)

    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    implementation(libs.floatingactionbutton)

    implementation(libs.volley)

    implementation(libs.play.services.auth)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}