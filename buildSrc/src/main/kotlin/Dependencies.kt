object Versions {
    const val okHttp = "4.5.0"
    const val arrow_version = "0.8.2"
    const val daggerAssistedInject = "0.5.0"
    const val daggerVersion = "2.25.4"
    const val moshi_version = "1.12.0"
    const val okReplay = "1.5.0"
    const val glide_version = "4.11.0"
    const val markdown = "4.5.1"
    const val kotlin = "1.5.21"
    const val lifecycle_extensions = "2.2.0"
    const val koin = "2.2.3"
    const val room = "2.4.0-alpha03"
    const val retrofit = "2.8.1"
    const val rxBinding = "3.0.0"
    const val coroutines = "1.4.3"
    const val epoxy_version = "3.11.0"
    const val multidex = "2.0.1"
    const val nanohttpd = "2.3.1"
    const val jsoup = "1.15.3"
    const val uaUtils = "1.21"
    const val jgit = "6.4.0.202211300538-r" // no-update
    const val playServicesLicensesPlugin = "17.0.0"
    const val  mpchart = "3.0.3"
}

object RxJavaDependencies {
    const val rxBindingMaterial =
        "com.jakewharton.rxbinding3:rxbinding-material:${Versions.rxBinding}"
    const val rxBindingAppCompat =
        "com.jakewharton.rxbinding3:rxbinding-appcompat:${Versions.rxBinding}"
    const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxBinding}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.10"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:2.1.1"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.3.0"
}

object KotlinDependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
}

object DaggerDependencies {
    const val assistedInjectProcessor =
        "com.squareup.inject:assisted-inject-processor-dagger2:${Versions.daggerAssistedInject}"
    const val assistedInjectAnnotation =
        "com.squareup.inject:assisted-inject-annotations-dagger2:${Versions.daggerAssistedInject}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
    const val dagger = "com.google.dagger:dagger:${Versions.daggerVersion}"
}

object AndroidxDependencies {
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    const val slidingPaneLayout = "androidx.slidingpanelayout:slidingpanelayout:1.1.0"
    const val work = "androidx.work:work-runtime-ktx:2.4.0"
    const val emoji = "androidx.emoji:emoji:1.0.0"
    const val preference = "androidx.preference:preference:1.1.1"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val lifecycleLiveDataKtx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle_extensions}"
    const val lifecycleLiveData =
        "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycle_extensions}"
    const val lifecycleViewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_extensions}"
    const val lifecycleViewModel =
        "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycle_extensions}"
    const val lifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle_extensions}"
    const val lifecyclerRuntime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle_extensions}"
    const val lifecyclerCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle_extensions}"
    const val lifecycleRx =
        "androidx.lifecycle:lifecycle-reactivestreams:${Versions.lifecycle_extensions}"
    const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.3.0"
    const val navigationUIKtx = "androidx.navigation:navigation-ui-ktx:2.3.0"
    const val navigationUI = "androidx.navigation:navigation-ui:2.3.0"
    const val navigationFragment = "androidx.navigation:navigation-fragment:2.3.0"
    const val coreKtx = "androidx.core:core-ktx:1.3.0"
    const val appcompat = "androidx.appcompat:appcompat:1.2.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.1"
    const val legacyV4 = "androidx.legacy:legacy-support-v4:1.0.0"
    const val browser = "androidx.browser:browser:1.3.0"
    const val multidex = "androidx.multidex:multidex:${Versions.multidex}"
}

object MaterialDesignDependencies {
    const val materialDesign = "com.google.android.material:material:1.4.0"
}

object OkReplayDependencies {

    const val espresso = "com.airbnb.okreplay:espresso:${Versions.okReplay}"
    const val okReplayNoOp = "com.airbnb.okreplay:noop:${Versions.okReplay}"
    const val okReplay = "com.airbnb.okreplay:okreplay:${Versions.okReplay}"
}

object GooglePlayDependencies {
    const val playCore = "com.google.android.play:core:1.10.0"
    const val installReferrer = "com.android.installreferrer:installreferrer:2.2"
    const val playServicesLicenses =  "com.google.android.gms:play-services-oss-licenses:${Versions.playServicesLicensesPlugin}"
}

object GMSDependencies {
    const val auth = "com.google.android.gms:play-services-auth:18.1.0"
}

object KoinDependencies {
    const val koinAndroid = "io.insert-koin:koin-android:${Versions.koin}"
    const val koinViewModel = "io.insert-koin:koin-androidx-viewmodel:${Versions.koin}"
}

object GlideDependencies {
    const val okhttp = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide_version}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide_version}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide_version}"
}

object TestDependencies {
    const val annotation = "androidx.annotation:annotation:1.1.0"
    const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
    const val androidxJUnit = "androidx.test.ext:junit:1.1.2"
    const val jUnit = "junit:junit:4.13"
}

object MiscellaneousDependencies {
    const val youtubePlayer = "com.pierfrancescosoffritti.androidyoutubeplayer:core:10.0.5"
    const val AutoService = "com.google.auto.service:auto-service:1.0-rc7"
    const val htmlCompressor = "com.googlecode.htmlcompressor:htmlcompressor:1.4"
    const val markdownHtml = "io.noties.markwon:html:${Versions.markdown}"
    const val markdownCore = "io.noties.markwon:core:${Versions.markdown}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy_version}"
    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy_version}"
    const val realmFieldNamesHelper = "dk.ilios:realmfieldnameshelper:1.1.1"
    const val realm = "com.github.Zhuinden:realm-monarchy:0.5.1"
    const val stetho = "com.facebook.stetho:stetho-okhttp3:1.5.1"
    const val eventBus = "org.greenrobot:eventbus:3.1.1"
    const val olm = "org.matrix.gitlab.matrix-org:olm:3.1.2"
    const val textDrawable = "com.amulyakhare:com.amulyakhare.textdrawable:1.0.1"
    const val span = "me.gujun.android:span:1.7"
    const val threeTenABP = "com.jakewharton.threetenabp:threetenabp:1.0.3"
    const val linkMovement = "me.saket:better-link-movement-method:2.2.0"
    const val libPhoneNumber = "com.googlecode.libphonenumber:libphonenumber:8.10.23"
    const val photoView = "com.github.chrisbanes:PhotoView:2.0.0"
    const val alerter = "com.tapadoo.android:alerter:5.1.2"
    const val timber = "com.jakewharton.timber:timber:5.0.1"
    const val markdownView = "com.github.GrenderG:MarkdownView:0.1.2"
    const val shimmer = "com.facebook.shimmer:shimmer:0.5.0"
    const val nanohttpd = "org.nanohttpd:nanohttpd-webserver:${Versions.nanohttpd}"
    const val jgit = "org.eclipse.jgit:org.eclipse.jgit:${Versions.jgit}"
    const val jsoup = "org.jsoup:jsoup:${Versions.jsoup}"
    const val uaUtils  = "eu.bitwalker:UserAgentUtils:${Versions.uaUtils}"
    const val mpchart = "com.github.PhilJay:MPAndroidChart:${Versions.mpchart}"
}

object FirebaseDependencies {
    const val inAppMessagingKtx = "com.google.firebase:firebase-inappmessaging-display-ktx:20.1.1"
    const val dynamicLinksKtx = "com.google.firebase:firebase-dynamic-links-ktx:19.1.0"
    const val messaging = "com.google.firebase:firebase-messaging:20.2.4"
    const val configKtx = "com.google.firebase:firebase-config-ktx:19.2.0"
    const val commonKtx = "com.google.firebase:firebase-common-ktx:19.3.1"
    const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx:17.2.1"
    const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx:20.0.0"
    const val perfKtx ="com.google.firebase:firebase-perf-ktx:20.0.6"
}

object RetrofitDependencies {
    const val moshiKapt = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi_version}"
    const val moshiAdapter = "com.squareup.moshi:moshi-adapters:${Versions.moshi_version}"
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi_version}"
    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
}

object ArrowPreferences {
    const val arrowCore = "io.arrow-kt:arrow-instances-core:${Versions.arrow_version}"
    const val arrow = "io.arrow-kt:arrow-core:${Versions.arrow_version}"
}

object BuildConfigVersions {
    const val compileSdkVersion = 30
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val versionCode = 36
    const val versionName = "1.5.6"
    const val applicationId = "org.merakilearn"
}

object Classpath {
    const val gradle = "com.android.tools.build:gradle:7.0.2"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinExtensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    // Newer versions may be available: please check here:
    // https://chaquo.com/chaquopy/doc/current/changelog.html
    const val python = "com.chaquo.python:gradle:10.0.1"
    const val realm = "io.realm:realm-gradle-plugin:6.1.0"
    const val googleServices = "com.google.gms:google-services:4.3.4"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.4.1"
    const val firebasePerf= "com.google.firebase.firebase-perf:com.google.firebase.firebase-perf.gradle.plugin:1.4.1"

   const val benManes = "com.github.ben-manes:gradle-versions-plugin:0.20.0"
}

object Plugins {
    const val application = "com.android.application"
    const val library = "com.android.library"
    const val dynamicFeature = "com.android.dynamic-feature"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val gms = "com.google.gms.google-services"
    const val crashlytics = "com.google.firebase.crashlytics"
    const val realm = "realm-android"
    const val python = "com.chaquo.python"
    const val perf = "com.google.firebase.firebase-perf"
    const val kotlinJetbrainAndroid = "org.jetbrains.kotlin.android"
    const val githubBenManes = "com.github.ben-manes.versions"
}