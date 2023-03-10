plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
    id(Plugins.realm)
}

kapt {
    correctErrorTypes = true
}

android {
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":commonUI"))
    implementation(project(":core"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*jar"))))

    //Kotlin
    implementation(KotlinDependencies.kotlin)
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    //Androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.lifecycleRx)
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.lifecycleViewModelKtx)
    implementation(AndroidxDependencies.legacyV4)
    implementation(AndroidxDependencies.emoji)
    // Pref
    implementation(AndroidxDependencies.preference)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    //Material
    implementation(MaterialDesignDependencies.materialDesign)

    // DI
    implementation(DaggerDependencies.dagger)
    kapt(DaggerDependencies.daggerCompiler)
    compileOnly(DaggerDependencies.assistedInjectAnnotation)
    kapt(DaggerDependencies.assistedInjectProcessor)

    //Matrix
    compileOnly(files("lib/matrix-sdk-android-release.aar"))
    compileOnly(files("lib/matrix-sdk-android-rx-release.aar"))

    //Glide
    implementation(GlideDependencies.glide)
    implementation(GlideDependencies.okhttp)
    kapt(GlideDependencies.glideCompiler)


    // Work
    implementation(AndroidxDependencies.work)

    //GMS
    implementation(GMSDependencies.base)

    // Network
    implementation(RetrofitDependencies.retrofit)
    implementation(RetrofitDependencies.moshiConverter)
    implementation(RetrofitDependencies.okHttp)
    implementation(RetrofitDependencies.logging)
    implementation(RetrofitDependencies.moshiAdapter)
    kapt(RetrofitDependencies.moshiKapt)

    // FP
    implementation(ArrowPreferences.arrow)
    implementation(ArrowPreferences.arrowCore)

    // Logging
    implementation(MiscellaneousDependencies.timber)
    implementation(MiscellaneousDependencies.stetho)

    debugImplementation(OkReplayDependencies.okReplay)
    releaseImplementation(OkReplayDependencies.okReplayNoOp)
    androidTestImplementation(OkReplayDependencies.espresso)

    implementation(MiscellaneousDependencies.markdownCore)
    implementation(MiscellaneousDependencies.markdownHtml)
    implementation(MiscellaneousDependencies.htmlCompressor)

    // rx
    implementation(RxJavaDependencies.rxKotlin)
    implementation(RxJavaDependencies.rxAndroid)
    implementation(RxJavaDependencies.rxRelay)

    //firebase
    implementation(FirebaseDependencies.messaging)

    // Bus
    implementation(MiscellaneousDependencies.eventBus)

    // olm lib is now hosted by jitpack: https://jitpack.io/#org.matrix.gitlab.matrix-org/olm
    implementation(MiscellaneousDependencies.olm)

    // Database
    implementation(MiscellaneousDependencies.realm)
    kapt(MiscellaneousDependencies.realmFieldNamesHelper)

    //Epoxy
    implementation(MiscellaneousDependencies.epoxy)
    kapt(MiscellaneousDependencies.epoxyProcessor)

    // RXBinding
    implementation(RxJavaDependencies.rxBinding)
    implementation(RxJavaDependencies.rxBindingAppCompat)
    implementation(RxJavaDependencies.rxBindingMaterial)

    //textDrawable
    implementation(MiscellaneousDependencies.textDrawable)

    implementation(MiscellaneousDependencies.span)

    implementation(MiscellaneousDependencies.threeTenABP)

    implementation(MiscellaneousDependencies.linkMovement)

    // Phone number https://github.com/google/libphonenumber
    implementation(MiscellaneousDependencies.libPhoneNumber)

    implementation(MiscellaneousDependencies.photoView)

    //Alerter
    implementation(MiscellaneousDependencies.alerter)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)
}