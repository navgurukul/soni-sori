plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinJetbrainAndroid)
    id(Plugins.kotlinKapt)
    id(Plugins.kotlinAndroid)
}
android {
    namespace = "org.merakilearn.scratchjr"
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))
    implementation(project(mapOf("path" to ":commonUI")))

    implementation ("com.google.auto.service:auto-service:1.0.1")
    kapt ("com.google.auto.service:auto-service:1.0.1")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.gms:play-services-analytics:18.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playFeatureDeliveryLibrary)
    implementation(GooglePlayDependencies.extensionsForFeatureLibrary)

    //Firebase
    implementation(FirebaseDependencies.analyticsKtx)
    implementation(FirebaseDependencies.crashlyticsKtx)

    implementation("com.google.android.gms:play-services-location:21.3.0")
}