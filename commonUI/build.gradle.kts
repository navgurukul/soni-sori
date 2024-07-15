plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
//    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinParcelize)
}

android {
    namespace = "org.navgurukul.commonui"
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
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
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
    implementation(GooglePlayDependencies.playFeatureDeliveryLibrary)
    implementation(GooglePlayDependencies.extensionsForFeatureLibrary)

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

    // glide
    implementation(GlideDependencies.glide)
    implementation(GlideDependencies.okhttp)
    implementation ("com.caverock:androidsvg:1.4")

}