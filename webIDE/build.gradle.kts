plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinJetbrainAndroid)
    id(Plugins.kotlinAndroid)
//    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinParcelize)
    id(Plugins.kotlinKapt)
    id(Plugins.githubBenManes)
}
android {
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    packagingOptions {
        exclude("**/*.txt")
        exclude("**/*.xml")
        exclude( "**/*.properties")
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "org.navgurukul.webide"
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))
    implementation(project(mapOf("path" to ":commonUI")))

//    kapt(MiscellaneousDependencies.AutoService)
//    implementation(MiscellaneousDependencies.AutoService)

    implementation ("com.google.auto.service:auto-service:1.0.1")
    kapt ("com.google.auto.service:auto-service:1.0.1")

//    val multidexVersion = "2.0.1"
//    implementation("androidx.multidex:multidex:$multidexVersion")

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playFeatureDeliveryLibrary)
    implementation(GooglePlayDependencies.extensionsForFeatureLibrary)

    implementation(KotlinDependencies.kotlin)
    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)

    //lifecycle
    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.lifecycleViewModelKtx)
    implementation(AndroidxDependencies.lifecycleLiveDataKtx)

    implementation(AndroidxDependencies.lifecyclerRuntime)
    kapt(AndroidxDependencies.lifecyclerCompiler)

    implementation(AndroidxDependencies.multidex)
    implementation(AndroidxDependencies.preference)

    implementation (MiscellaneousDependencies.nanohttpd)
    implementation (MiscellaneousDependencies.jsoup)
    implementation (MiscellaneousDependencies.uaUtils)
    implementation(MiscellaneousDependencies.jgit) {
        exclude(module= "httpclient")
        exclude(group = "org.apache.httpcomponents")
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
    }

//    implementation (GooglePlayDependencies.playServicesLicenses)
    implementation (MiscellaneousDependencies.mpchart)

    // Logging
    implementation(MiscellaneousDependencies.timber)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)
}