import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.BaseVariantOutput
import de.undercouch.gradle.tasks.download.Download

plugins {
    id(Plugins.application)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
    id(Plugins.gms)
    id(Plugins.crashlytics)
    id(Plugins.perf)
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.merakilearn"
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        applicationId = BuildConfigVersions.applicationId
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion
        versionCode = BuildConfigVersions.versionCode
        versionName = BuildConfigVersions.versionName

        ndk {
            abiFilters.clear()
            abiFilters.add("arm64-v8a")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "SERVER_URL", "\"https://api.merakilearn.org/\"")
        }

        getByName("debug") {
            buildConfigField("String", "SERVER_URL", "\"https://merd-api.merakilearn.org/\"")
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
        buildConfig = true
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }
    // This specifies the dynamic features.
    dynamicFeatures.add(":typing")

    bundle {
        abi {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to "../chat/lib", "include" to listOf("*.jar"))))

    //modules
    implementation(project(":learn"))
    implementation(project(":chat"))
    implementation(project(":python"))
    implementation(project(":core"))
    implementation(project(":commonUI"))

    //AndroidX
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.legacyV4)
    implementation(AndroidxDependencies.preference)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Matrix
    implementation(files("../chat/lib/matrix-sdk-android-release.aar"))
    implementation(files("../chat/lib/matrix-sdk-android-rx-release.aar"))

    //Navigation
    implementation(AndroidxDependencies.navigationFragment)
    implementation(AndroidxDependencies.navigationUI)
    implementation(AndroidxDependencies.navigationUIKtx)
    implementation(AndroidxDependencies.navigationFragmentKtx)

    //lifecycle
    implementation(AndroidxDependencies.lifecycleExtensions)
    implementation(AndroidxDependencies.lifecycleViewModelKtx)
    implementation(AndroidxDependencies.lifecycleLiveDataKtx)

    //Moshi
    implementation(RetrofitDependencies.moshiAdapter)
    implementation(RetrofitDependencies.moshiConverter)
    kapt(RetrofitDependencies.moshiKapt)

    // Room DB
    implementation(AndroidxDependencies.roomRuntime)
    implementation(AndroidxDependencies.roomKtx)
    kapt(AndroidxDependencies.roomCompiler)

    //google auth
    implementation(GMSDependencies.auth)

    //Miscellaneous
    implementation(MiscellaneousDependencies.markdownView)
    // Logging
    implementation(MiscellaneousDependencies.timber)

    // Retrofit
    implementation(RetrofitDependencies.retrofit)
    implementation(RetrofitDependencies.logging)

    //firebase
    implementation(FirebaseDependencies.analyticsKtx)
    implementation(FirebaseDependencies.crashlyticsKtx)
    implementation(FirebaseDependencies.messaging)
    implementation(FirebaseDependencies.dynamicLinksKtx)
    implementation(FirebaseDependencies.inAppMessagingKtx)
    implementation(FirebaseDependencies.perfKtx)

    //glide
    implementation(GlideDependencies.glide)
    kapt(GlideDependencies.glideCompiler)
    implementation (GlideDependencies.glideSvg)

    //Google play
    implementation(GooglePlayDependencies.playCore)
    implementation(GooglePlayDependencies.installReferrer)


    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    implementation ("com.amazonaws:aws-android-sdk-s3:2.22.+")
    implementation ("com.amazonaws:aws-android-sdk-mobile-client:2.22.+")

    implementation ("com.google.android.gms:play-services-auth:19.0.0")

    //lottie
    implementation ("com.airbnb.android:lottie:4.2.0")

    //chucker
    debugImplementation ("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation ("com.github.chuckerteam.chucker:library-no-op:3.5.2")
}

tasks.register<Download>("downloadBundleTools") {
    src("https://github.com/google/bundletool/releases/download/1.17.2/bundletool-all-1.17.2.jar")
    dest(File(buildDir, "bundletool-all-1.17.2.jar"))
}

android.applicationVariants.all {
    outputs.forEach { output: BaseVariantOutput? ->
        (output as? ApkVariantOutput)?.let { apkOutput: ApkVariantOutput ->
            val variantName = this.name.substring(0, 1).toUpperCase() + this.name.substring(1)
            val bundleToolJar = File(buildDir, "bundletool-all-1.17.2.jar")
            val bundleFile = File(buildDir, "outputs/bundle/${this.name}/app-${this.name}.aab")
            val outputFile = File(buildDir, "outputs/apks/${this.name}/app-${this.name}.apks")

            val signingInfo = android.signingConfigs.find { it.name == this.name }

            tasks.register<JavaExec>("buildApks$variantName") {
                classpath = files(bundleToolJar)
                val argsList = arrayListOf(
                    "build-apks",
                    "--overwrite",
                    "--bundle",
                    bundleFile.absolutePath,
                    "--output",
                    outputFile.absolutePath
                ).apply {
                    if (signingInfo != null) {
                        addAll(
                            arrayListOf(
                                "--ks",
                                signingInfo.storeFile!!.absolutePath,
                                "--ks-pass",
                                "pass:${signingInfo.storePassword!!}",
                                "--ks-key-alias",
                                signingInfo.keyAlias!!,
                                "--key-pass",
                                "pass:${signingInfo.keyPassword!!}"
                            )
                        )
                    }
                }

                args = argsList

                if (!bundleToolJar.exists()) {
                    dependsOn("downloadBundleTools")
                }
                dependsOn("bundle$variantName")
            }

            tasks.register<JavaExec>("installApkSplitsForTest$variantName") {
                classpath = files(bundleToolJar)
                args = listOf("install-apks", "--apks", outputFile.absolutePath)
                dependsOn("buildApks$variantName")
            }
        }
    }
}