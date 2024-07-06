plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
}

android {
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(KotlinDependencies.kotlin)

    // AndroidX
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.browser)
    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.lifecycleRx)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.lifecycleViewModelKtx)
    implementation(AndroidxDependencies.legacyV4)
    implementation(AndroidxDependencies.emoji)
    implementation(AndroidxDependencies.preference)
    implementation(AndroidxDependencies.work)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Kotlin
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    // Retrofit
    implementation(RetrofitDependencies.moshiAdapter)
    implementation(RetrofitDependencies.retrofit)
    implementation(RetrofitDependencies.moshiConverter)
    implementation(RetrofitDependencies.okHttp)
    implementation(RetrofitDependencies.logging)
    kapt(RetrofitDependencies.moshiKapt)

    // Google Play
    implementation(GooglePlayDependencies.playCore)

    // Firebase
    implementation(FirebaseDependencies.messaging)
    implementation(FirebaseDependencies.configKtx)
    implementation(FirebaseDependencies.commonKtx)

    // Logging
    implementation(MiscellaneousDependencies.timber)
    implementation(MiscellaneousDependencies.stetho)

    // Miscellaneous
    implementation(MiscellaneousDependencies.markdownCore)
    implementation(MiscellaneousDependencies.markdownHtml)
    implementation(MiscellaneousDependencies.htmlCompressor)
    implementation(MiscellaneousDependencies.eventBus)
    implementation(MiscellaneousDependencies.olm)
    implementation(MiscellaneousDependencies.realm)
    kapt(MiscellaneousDependencies.realmFieldNamesHelper)
    implementation(MiscellaneousDependencies.epoxy)
    kapt(MiscellaneousDependencies.epoxyProcessor)
    implementation(MiscellaneousDependencies.textDrawable)
    implementation(MiscellaneousDependencies.span)
    implementation(MiscellaneousDependencies.threeTenABP)
    implementation(MiscellaneousDependencies.linkMovement)
    implementation(MiscellaneousDependencies.libPhoneNumber)
    implementation(MiscellaneousDependencies.photoView)
    implementation(MiscellaneousDependencies.alerter)

    // Matrix
    compileOnly(files("lib/matrix-sdk-android-release.aar"))
    compileOnly(files("lib/matrix-sdk-android-rx-release.aar"))

    // Glide
    implementation(GlideDependencies.glide)
    implementation(GlideDependencies.okhttp)
    kapt(GlideDependencies.glideCompiler)
    implementation(GlideDependencies.glideSvg)

    // GMS
    implementation(GMSDependencies.base)

    // Arrow
    implementation(ArrowPreferences.arrow)
    implementation(ArrowPreferences.arrowCore)

    // OkReplay
    debugImplementation(OkReplayDependencies.okReplay)
    releaseImplementation(OkReplayDependencies.okReplayNoOp)
    androidTestImplementation(OkReplayDependencies.espresso)

    // RxJava
    implementation(RxJavaDependencies.rxKotlin)
    implementation(RxJavaDependencies.rxAndroid)
    implementation(RxJavaDependencies.rxRelay)
    implementation(RxJavaDependencies.rxBinding)
    implementation(RxJavaDependencies.rxBindingAppCompat)
    implementation(RxJavaDependencies.rxBindingMaterial)

    // Test dependencies
    testImplementation(TestDependencies.jUnit)
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)

    // Project modules
    implementation(project(":commonUI"))
}
