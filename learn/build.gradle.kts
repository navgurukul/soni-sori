plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
}

android {
    compileSdkVersion(BuildConfigVersions.compileSdkVersion)
    buildToolsVersion(BuildConfigVersions.buildToolsVersion)

    defaultConfig {
        minSdkVersion(BuildConfigVersions.minSdkVersion)
        targetSdkVersion(BuildConfigVersions.targetSdkVersion)
        versionCode = BuildConfigVersions.versionCode
        versionName = BuildConfigVersions.versionName

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    signingConfigs {
        getByName("debug") {
            storeFile(file("debug.keystore"))
        }
    }

    buildTypes {
        getByName("release") {
            buildConfigField("int", "VERSION_CODE", "${BuildConfigVersions.versionCode}")
        }

        getByName("debug") {
            buildConfigField("int", "VERSION_CODE", "${BuildConfigVersions.versionCode}")
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
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
    kapt(AndroidxDependencies.roomCompiler)

    implementation(MiscellaneousDependencies.textDrawable)
    implementation(MiscellaneousDependencies.markdownView)

    // Coroutines
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    // Retrofit
    implementation(RetrofitDependencies.retrofit)
    implementation(RetrofitDependencies.moshiAdapter)
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
}