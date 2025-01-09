plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
//    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinParcelize)
    id(Plugins.python)
}

android {
    namespace = "org.navgurukul.playground"
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion

        ndk {
            abiFilters.clear()
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(project(":commonUI"))
    implementation(project(":core"))

    implementation(AndroidxDependencies.multidex)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.constraintLayout)

    //material
    implementation(MaterialDesignDependencies.materialDesign)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    //firebase
    implementation(FirebaseDependencies.crashlyticsKtx)
    implementation(platform(FirebaseDependencies.firebaseBom))

    //kotlin
    implementation(KotlinDependencies.kotlin)
    implementation(KotlinDependencies.coroutinesCore)
    implementation(KotlinDependencies.coroutinesAndroid)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)
}