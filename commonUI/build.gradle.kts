plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = BuildConfigVersions.compileSdkVersion

    namespace = "org.navgurukul.commonui"

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
        buildConfig = true
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
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(KotlinDependencies.kotlin)
    //androidX
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.lifecycleLiveDataKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.constraintLayout)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    // rx
    implementation(RxJavaDependencies.rxKotlin)
    implementation(RxJavaDependencies.rxJava)

    //Material
    implementation(MaterialDesignDependencies.materialDesign)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Logging
    implementation(MiscellaneousDependencies.timber)

    // RXBinding
    implementation(RxJavaDependencies.rxBinding)

    implementation(MiscellaneousDependencies.shimmer)
    implementation("androidx.core:core-ktx:+")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

}