plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    //id(Plugins.kotlinExtensions)
    id(BuildPlugins.kotlinKapt)
    //id(Plugins.KotlinParcelize)
    id(BuildPlugins.kotlinParcelizePlugin)
}

android {
    compileSdk = AndroidSdk.compileSdkVersion

    defaultConfig {
        minSdk = AndroidSdk.minSdkVersion
        targetSdk = AndroidSdk.targetSdkVersion

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
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
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

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Kotlin
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    implementation(RetrofitDependencies.moshiAdapter)
    kapt(RetrofitDependencies.moshiKapt)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    //firebase
    implementation(FirebaseDependencies.messaging)
    implementation(FirebaseDependencies.configKtx)
    implementation(FirebaseDependencies.commonKtx)

    //Logging
    implementation(MiscellaneousDependencies.timber)

    testImplementation(TestDependencies.jUnit)
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)

}