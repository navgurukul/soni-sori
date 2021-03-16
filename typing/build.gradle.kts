plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
    id(Plugins.gms)
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
    implementation(project(":app"))
    implementation(project(":core"))
    implementation(project(":commonUI"))

    kapt(MiscellaneousDependencies.AutoService)
    implementation(MiscellaneousDependencies.AutoService)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    //to get dynamic feature module
    implementation(PlayCoreDependencies.playCore)

    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.legacyV4)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)
}