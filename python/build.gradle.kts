plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.python)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    compileSdk = BuildConfigVersions.compileSdkVersion

    namespace = "org.navgurukul.playground"

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
        jvmTarget = "18"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_18)
        targetCompatibility(JavaVersion.VERSION_18)
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":commonUI"))
    implementation(project(":core"))

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

    implementation(FirebaseDependencies.crashlyticsKtx)

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