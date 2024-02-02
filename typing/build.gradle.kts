
plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
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
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
}

dependencies {
    implementation(project(":commonUI"))
    implementation(project(":app"))
    implementation(project(":core"))

    kapt(MiscellaneousDependencies.AutoService)
    implementation(MiscellaneousDependencies.AutoService)

    // Logging
    implementation(MiscellaneousDependencies.timber)
    dependencies {
        implementation ("com.jakewharton.timber:timber:5.0.1")
    }

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.legacyV4)

    // Logging
    implementation(MiscellaneousDependencies.timber)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)
}