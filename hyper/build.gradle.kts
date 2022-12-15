plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinJetbrainAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
}
android {
    namespace = "org.navgurukul.hyper"
    compileSdk = 32

    defaultConfig {
        minSdk = 21
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
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))

    kapt(MiscellaneousDependencies.AutoService)
    implementation(MiscellaneousDependencies.AutoService)

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