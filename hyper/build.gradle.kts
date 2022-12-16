plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinJetbrainAndroid)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
    id(Plugins.githubBenManes)
    id(Plugins.gmsOSSLicense)
}
android {
    namespace = "org.navgurukul.hyper"
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

//    packagingOptions {
//        exclude("**/*.txt")
//        exclude("**/*.xml")
//        exclude( "**/*.properties")
//    }
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))

    kapt(MiscellaneousDependencies.AutoService)
    implementation(MiscellaneousDependencies.AutoService)
    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    implementation(KotlinDependencies.kotlin)
    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)

    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.lifecyclerRuntime)
    kapt(AndroidxDependencies.lifecyclerCompiler)

    implementation(AndroidxDependencies.multidex)
    implementation(AndroidxDependencies.preference)

    implementation (MiscellaneousDependencies.nanohttpd)
    implementation (MiscellaneousDependencies.jsoup)
    implementation (MiscellaneousDependencies.uaUtils)
    implementation(MiscellaneousDependencies.jgit)
//    {
//        exclude(module: 'httpclient')
//        exclude group: 'org.apache.httpcomponents',
//        exclude group: "org.apache.httpcomponents", module: "httpclient"
//    }

    implementation (GooglePlayDependencies.playServicesLicenses)
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