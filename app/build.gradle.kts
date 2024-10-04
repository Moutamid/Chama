plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.moutamid.chamaaa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moutamid.chamaaa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "Chama-$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { viewBinding = true }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")

        // You can also exclude other conflicting files if needed
        // exclude 'META-INF/LICENSE'
        // exclude 'META-INF/LICENSE.txt'
        // exclude 'META-INF/NOTICE'
        // exclude 'META-INF/NOTICE.txt'
    }

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
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")

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