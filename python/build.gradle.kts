plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.python)
}

android {
    namespace = "org.navgurukul.playground"
    compileSdk = BuildConfigVersions.compileSdkVersion
    ndkVersion = BuildConfigVersions.ndkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion

        ndk {
            abiFilters.clear()
            abiFilters.add("arm64-v8a")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
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