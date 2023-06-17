plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinKapt)
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    compileSdk = BuildConfigVersions.compileSdkVersion
    namespace = "org.merakilearn.learn"
    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
    }

    buildTypes {
        getByName("release") {
            buildConfigField("int", "VERSION_CODE", "${BuildConfigVersions.versionCode}")
        }

        getByName("debug") {
            buildConfigField("int", "VERSION_CODE", "${BuildConfigVersions.versionCode}")
        }

        buildFeatures {
            viewBinding = true
            buildConfig = true
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":core"))
    implementation(project(":commonUI"))

    //kotlin
    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.slidingPaneLayout)
    implementation(AndroidxDependencies.preference)
    implementation(AndroidxDependencies.swipeRefreshLayout)

    // Lifecycle and Viewmodels
    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.lifecycleViewModel)
    implementation(AndroidxDependencies.lifecycleLiveData)
    implementation(AndroidxDependencies.lifecycleViewModelKtx)
    implementation(AndroidxDependencies.lifecycleLiveDataKtx)

//    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Room DB
    implementation(AndroidxDependencies.roomRuntime)
    implementation(AndroidxDependencies.roomKtx)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
    implementation("androidx.core:core-ktx:+")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    kapt(AndroidxDependencies.roomCompiler)

    implementation(MiscellaneousDependencies.textDrawable)
    implementation(MiscellaneousDependencies.markdownView)

    // Coroutines
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    // Retrofit
    implementation(RetrofitDependencies.retrofit)
    implementation(RetrofitDependencies.moshiAdapter)
    implementation(RetrofitDependencies.moshiKotlin)
    implementation(RetrofitDependencies.moshiConverter)
    kapt(RetrofitDependencies.moshiKapt)
    implementation(RetrofitDependencies.logging)


    implementation(MiscellaneousDependencies.youtubePlayer)

    //glide
    implementation(GlideDependencies.glide)
    kapt(GlideDependencies.glideCompiler)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)

    //firebase
    implementation(FirebaseDependencies.perfKtx)

}