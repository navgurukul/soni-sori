import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.BaseVariantOutput
import java.util.Properties
//import de.undercouch.gradle.tasks.download.Download

plugins {
    id(Plugins.application)
    id(Plugins.kotlinAndroid)
//    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
    id(Plugins.kotlinParcelize)
    id(Plugins.gms)
    id(Plugins.crashlytics)
    id("org.jetbrains.kotlin.android")
}

android {

    compileSdk = BuildConfigVersions.compileSdkVersion



    defaultConfig {
        applicationId = BuildConfigVersions.applicationId
        minSdk = BuildConfigVersions.minSdkVersion
        targetSdk = BuildConfigVersions.targetSdkVersion
        versionCode = BuildConfigVersions.versionCode
        versionName = BuildConfigVersions.versionName
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val propsFile = rootProject.file("local.properties")
            if (propsFile.exists()) {
                val props = Properties().apply {
                    propsFile.inputStream().use { load(it) }
                }
                if (props.containsKey("STORE_FILE")) {
                    storeFile = file(props["STORE_FILE"] as String)
                    storePassword = props["STORE_PASSWORD"] as String
                    keyAlias = props["KEY_ALIAS"] as String
                    keyPassword = props["KEY_PASSWORD"] as String
                }
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "SERVER_URL", "\"https://api.merakilearn.org/\"")
        }

        getByName("debug") {
            buildConfigField("String", "SERVER_URL", "\"https://api.merakilearn.org/\"")
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
                nativeSymbolUploadEnabled = false
            }
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    packagingOptions {
        resources {
            merges += setOf("/META-INF/services/*")
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
    }

    // This specifies the dynamic features.
    dynamicFeatures.add(":typing")
    dynamicFeatures += setOf(":webIDE")

    namespace = "org.merakilearn"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    implementation(fileTree(mapOf("dir" to "../chat/lib", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar","*.aar"))))

    //modules
    implementation(project(":learn"))
//    implementation(project(":chat"))
    implementation(project(":python"))
    implementation(project(":core"))
    implementation(project(":commonUI"))

    implementation ("com.google.auto.service:auto-service:1.1.1")
    kapt ("com.google.auto.service:auto-service:1.1.1")


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
//    implementation(files("../chat/lib/matrix-sdk-android-release.aar"))
//    implementation(files("../chat/lib/matrix-sdk-android-rx-release.aar"))

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
    implementation(platform(FirebaseDependencies.firebaseBom))
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
    implementation(GooglePlayDependencies.playFeatureDeliveryLibrary)
    implementation(GooglePlayDependencies.extensionsForFeatureLibrary)
    implementation(GooglePlayDependencies.playInAppUpdateLibrary)
    implementation(GooglePlayDependencies.extensionsForInAppUpdateLibrary)
    implementation(GooglePlayDependencies.installReferrer)

    //for webide
    implementation (MiscellaneousDependencies.uaUtils)
    implementation (MiscellaneousDependencies.nanohttpd)
    implementation (MiscellaneousDependencies.jsoup)

    implementation(AndroidxDependencies.multidex)

    //test
    testImplementation(TestDependencies.jUnit)

    //androidTest
    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
//    implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")

    implementation ("com.amazonaws:aws-android-sdk-s3:2.22.+")
    implementation ("com.amazonaws:aws-android-sdk-mobile-client:2.22.+")
    implementation("com.github.felHR85:UsbSerial:6.0.6")

    //implementation ("com.google.android.gms:play-services-auth:19.0.0")

    //lottie
    implementation ("com.airbnb.android:lottie:4.2.0")

    //chucker
    debugImplementation ("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation ("com.github.chuckerteam.chucker:library-no-op:3.5.2")
}

//tasks.register<Download>("downloadBundleTools") {
//    src("https://github.com/google/bundletool/releases/download/1.5.0/bundletool-all-1.5.0.jar")
//    dest(File(buildDir, "bundletool-all.jar"))
//}

// Auto-extract Scratch assets from zip before build
tasks.register<Copy>("extractScratchAssets") {
    val zipFile = rootProject.file("scratch-assets/build-wo-remix.zip")
    if (zipFile.exists()) {
        from(zipTree(zipFile))
        into(layout.projectDirectory.dir("src/main/assets"))
        eachFile {
            // strip the top-level folder name from zip
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
        includeEmptyDirs = false
    }
}

tasks.whenTaskAdded {
    if (name == "mergeDebugAssets" || name == "mergeReleaseAssets"
        || name == "generateReleaseLintModel" || name == "generateDebugLintModel"
        || name == "lintVitalAnalyzeRelease" || name == "generateReleaseLintVitalReportModel") {
        dependsOn("extractScratchAssets")
    }
}

android.applicationVariants.all {
    outputs.forEach { output: BaseVariantOutput? ->
        (output as? ApkVariantOutput)?.let { apkOutput: ApkVariantOutput ->
            val bundleToolJar = File(buildDir, "bundletool-all.jar")
            var filePath = apkOutput.outputFile.absolutePath
            filePath = filePath.replaceAfterLast(".", "aab")
            filePath = filePath.replace("build/outputs/apk/", "build/outputs/bundle/")
            var outputPath = filePath.replace("build/outputs/bundle/", "build/outputs/apks/")
            outputPath = outputPath.replaceAfterLast(".", "apks")

            val signingInfo = android.signingConfigs.find { it.name == this.name }

            tasks.register<JavaExec>("buildApks${this.name.capitalize()}") {
                classpath = files(bundleToolJar)
                val argsList = arrayListOf(
                    "build-apks",
                    "--overwrite",
                    "--local-testing",
                    "--bundle",
                    filePath,
                    "--output",
                    outputPath
                ).apply {
                    if (signingInfo != null && signingInfo.storeFile != null && signingInfo.storePassword != null && signingInfo.keyAlias != null && signingInfo.keyPassword != null) {
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
                dependsOn("bundle${this.name.capitalize()}")
            }

            tasks.register<JavaExec>("installApkSplitsForTest${this.name.capitalize()}") {
                classpath = files(bundleToolJar)
                args = listOf("install-apks", "--apks", outputPath)
                dependsOn("buildApks${this.name.capitalize()}")
            }
        }
    }
}

configurations.all {
    resolutionStrategy {
        force(AndroidxDependencies.lifecycleViewModelKtx)
        force(AndroidxDependencies.lifecycleViewModel)
    }
}