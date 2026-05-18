plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
//    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinParcelize)
    id(Plugins.kotlinKapt)
}

android {
    namespace = "org.navgurukul.core"
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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
    namespace = "org.merakilearn.core"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.browser)

    implementation(AndroidxDependencies.multidex)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Kotlin
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    implementation(RetrofitDependencies.moshiAdapter)
    kapt(RetrofitDependencies.moshiKapt)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playFeatureDeliveryLibrary)
    implementation(GooglePlayDependencies.extensionsForFeatureLibrary)

    //firebase
    implementation(platform(FirebaseDependencies.firebaseBom))
    implementation(FirebaseDependencies.messaging)
    implementation(FirebaseDependencies.configKtx)
    implementation(FirebaseDependencies.commonKtx)

    //Logging
    implementation(MiscellaneousDependencies.timber)

    testImplementation(TestDependencies.jUnit)
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)

}