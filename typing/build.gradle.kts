
plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinAndroid)
//    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinParcelize)
    id(Plugins.kotlinKapt)
    id(Plugins.gms)
}

android {
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
    buildFeatures{
        viewBinding = true
    }
    namespace = "org.navgurukul.typing"
}

dependencies {
    implementation(project(":commonUI"))
    implementation(project(":app"))
    implementation(project(":core"))

//    kapt(MiscellaneousDependencies.AutoService)
//    implementation(MiscellaneousDependencies.AutoService)

    implementation ("com.google.auto.service:auto-service:1.0.1")
    kapt ("com.google.auto.service:auto-service:1.0.1")

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playFeatureDeliveryLibrary)
    implementation(GooglePlayDependencies.extensionsForFeatureLibrary)

    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.legacyV4)

    //Timber
    implementation(MiscellaneousDependencies.timber)

    // Logging
    implementation(MiscellaneousDependencies.timber)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)
}