plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
}

android {
    compileSdkVersion(BuildConfigVersions.compileSdkVersion)
    buildToolsVersion(BuildConfigVersions.buildToolsVersion)

    defaultConfig {
        minSdkVersion(BuildConfigVersions.minSdkVersion)
        targetSdkVersion(BuildConfigVersions.targetSdkVersion)
        versionCode(BuildConfigVersions.versionCode)
        versionName(BuildConfigVersions.versionName)

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    implementation(RetrofitDependencies.gson)

    //to get dynamic feature module
    implementation(PlayCoreDependencies.playCore)

    //firebase
    implementation(FirebaseDependencies.messaging)

    testImplementation(TestDependencies.jUnit)
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)

}