object Versions {
    const val okHttp = "4.5.0"
    const val arrow_version = "0.8.2"
    const val daggerAssistedInject = "0.5.0"
    const val daggerVersion = "2.25.4"
    const val moshi_version = "1.12.0"
    const val okReplay = "1.5.0"
    const val glide_version = "4.11.0"
    const val glide_svg = "1.4"
    const val markdown = "4.5.1"
    const val kotlin = "1.5.21"
    const val lifecycle_extensions = "2.2.0"
    const val koin = "2.2.3"
    const val room = "2.4.0-alpha03"
    const val retrofit = "2.8.1"
    const val rxBinding = "3.0.0"
    const val coroutines = "1.4.3"
    const val epoxy_version = "3.11.0"
    const val rxJava = "2.2.10"
    const val rxAndroid = "2.1.1"
    const val rxRelay = "2.1.1"
    const val rxKotlin = "2.3.0"
    const val swipeRefreshLayout = "1.1.0"
    const val slidingPaneLayout = "1.1.0"
    const val work = "2.7.1"
    const val emoji = "1.0.0"
    const val preference = "1.1.1"
    const val navigationFragmentKtx = "2.3.0"
    const val navigationUIKtx = "2.3.0"
    const val navigationUI = "2.3.0"
    const val navigationFragment = "2.3.0"
    const val coreKtx = "1.3.0"
    const val appcompat = "1.2.0"
    const val constraintLayout = "2.0.1"
    const val legacyV4 = "1.0.0"
    const val browser = "1.3.0"
    const val materialDesign = "1.4.0"
    const val playCore = "1.10.0"
    const val installReferrer = "2.2"
    const val base = "18.1.0"
    const val auth = "18.1.0"
    const val annotation = "1.1.0"
    const val espresso = "3.3.0"
    const val androidxJUnit = "1.1.2"
    const val jUnit = "4.13"
    const val youtubePlayer = "10.0.5"
    const val AutoService = "1.0-rc7"
    const val htmlCompressor = "1.4"
    const val realmFieldNamesHelper = "1.1.1"
    const val realm = "0.5.1"
    const val stetho = "1.5.1"
    const val eventBus = "3.1.1"
    const val olm = "3.1.2"
    const val textDrawable = "1.0.1"
    const val span = "1.7"
    const val threeTenABP = "1.0.3"
    const val linkMovement = "2.2.0"
    const val libPhoneNumber = "8.10.23"
    const val photoView = "2.0.0"
    const val alerter = "5.1.2"
    const val timber = "5.0.1"
    const val markdownView = "0.1.2"
    const val shimmer = "0.5.0"
    const val inAppMessagingKtx = "20.1.1"
    const val dynamicLinksKtx = "19.1.0"
    const val messaging = "23.0.0"
    const val configKtx = "19.2.0"
    const val commonKtx = "19.3.1"
    const val crashlyticsKtx = "17.2.1"
    const val analyticsKtx = "20.0.0"
    const val perfKtx = "20.0.6"
    const val gradle = "7.0.2"
    const val python = "10.0.1"
    const val realm2 = "6.1.0"
    const val googleServices = "4.3.4"
    const val firebaseCrashlytics = "2.4.1"
    const val firebasePerf = "1.4.1"
}

object RxJavaDependencies {
    const val rxBindingMaterial =
        "com.jakewharton.rxbinding3:rxbinding-material:${Versions.rxBinding}"
    const val rxBindingAppCompat =
        "com.jakewharton.rxbinding3:rxbinding-appcompat:${Versions.rxBinding}"
    const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxBinding}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxRelay}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
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
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
    const val slidingPaneLayout = "androidx.slidingpanelayout:slidingpanelayout:${Versions.slidingPaneLayout}"
    const val work = "androidx.work:work-runtime-ktx:${Versions.work}"
    const val emoji = "androidx.emoji:emoji:${Versions.emoji}"
    const val preference = "androidx.preference:preference:${Versions.preference}"
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
    const val lifecycleRx =
        "androidx.lifecycle:lifecycle-reactivestreams:${Versions.lifecycle_extensions}"
    const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationFragmentKtx}"
    const val navigationUIKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigationUIKtx}"
    const val navigationUI = "androidx.navigation:navigation-ui:${Versions.navigationUI}"
    const val navigationFragment = "androidx.navigation:navigation-fragment:${Versions.navigationFragment}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val legacyV4 = "androidx.legacy:legacy-support-v4:${Versions.legacyV4}"
    const val browser = "androidx.browser:browser:${Versions.browser}"
}

object MaterialDesignDependencies {
    const val materialDesign = "com.google.android.material:material:${Versions.materialDesign}"
}

object OkReplayDependencies {
    const val espresso = "com.airbnb.okreplay:espresso:${Versions.okReplay}"
    const val okReplayNoOp = "com.airbnb.okreplay:noop:${Versions.okReplay}"
    const val okReplay = "com.airbnb.okreplay:okreplay:${Versions.okReplay}"
}

object GooglePlayDependencies {
    const val playCore = "com.google.android.play:core:${Versions.playCore}"
    const val installReferrer = "com.android.installreferrer:installreferrer:${Versions.installReferrer}"
}

object GMSDependencies {
    const val base = "com.google.android.gms:play-services-base:${Versions.base}"
    const val auth = "com.google.android.gms:play-services-auth:${Versions.auth}"
}

object KoinDependencies {
    const val koinAndroid = "io.insert-koin:koin-android:${Versions.koin}"
    const val koinViewModel = "io.insert-koin:koin-androidx-viewmodel:${Versions.koin}"
}

object GlideDependencies {
    const val okhttp = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide_version}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide_version}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide_version}"
    const val glideSvg = "com.caverock:androidsvg:${Versions.glide_svg}"
}

object TestDependencies {
    const val annotation = "androidx.annotation:annotation:${Versions.annotation}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val androidxJUnit = "androidx.test.ext:junit:${Versions.androidxJUnit}"
    const val jUnit = "junit:junit:${Versions.jUnit}"
}

object MiscellaneousDependencies {
    const val youtubePlayer = "com.pierfrancescosoffritti.androidyoutubeplayer:core:${Versions.youtubePlayer}"
    const val AutoService = "com.google.auto.service:auto-service:${Versions.AutoService}"
    const val htmlCompressor = "com.googlecode.htmlcompressor:htmlcompressor:${Versions.htmlCompressor}"
    const val markdownHtml = "io.noties.markwon:html:${Versions.markdown}"
    const val markdownCore = "io.noties.markwon:core:${Versions.markdown}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy_version}"
    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy_version}"
    const val realmFieldNamesHelper = "dk.ilios:realmfieldnameshelper:${Versions.realmFieldNamesHelper}"
    const val realm = "com.github.Zhuinden:realm-monarchy:${Versions.realm}"
    const val stetho = "com.facebook.stetho:stetho-okhttp3:${Versions.stetho}"
    const val eventBus = "org.greenrobot:eventbus:${Versions.eventBus}"
    const val olm = "org.matrix.gitlab.matrix-org:olm:${Versions.olm}"
    const val textDrawable = "com.amulyakhare:com.amulyakhare.textdrawable:${Versions.textDrawable}"
    const val span = "me.gujun.android:span:${Versions.span}"
    const val threeTenABP = "com.jakewharton.threetenabp:threetenabp:${Versions.threeTenABP}"
    const val linkMovement = "me.saket:better-link-movement-method:${Versions.linkMovement}"
    const val libPhoneNumber = "com.googlecode.libphonenumber:libphonenumber:${Versions.libPhoneNumber}"
    const val photoView = "com.github.chrisbanes:PhotoView:${Versions.photoView}"
    const val alerter = "com.tapadoo.android:alerter:${Versions.alerter}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val markdownView = "com.github.GrenderG:MarkdownView:${Versions.markdownView}"
    const val shimmer = "com.facebook.shimmer:shimmer:${Versions.shimmer}"
}

object FirebaseDependencies {
    const val inAppMessagingKtx = "com.google.firebase:firebase-inappmessaging-display-ktx:${Versions.inAppMessagingKtx}"
    const val dynamicLinksKtx = "com.google.firebase:firebase-dynamic-links-ktx:${Versions.dynamicLinksKtx}"
    const val messaging = "com.google.firebase:firebase-messaging:${Versions.messaging}"
    const val configKtx = "com.google.firebase:firebase-config-ktx:${Versions.configKtx}"
    const val commonKtx = "com.google.firebase:firebase-common-ktx:${Versions.commonKtx}"
    const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx:${Versions.crashlyticsKtx}"
    const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx:${Versions.analyticsKtx}"
    const val perfKtx = "com.google.firebase:firebase-perf-ktx:${Versions.perfKtx}"
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
    const val compileSdkVersion = 33
    const val minSdkVersion = 23
    const val targetSdkVersion = 33
    const val versionCode = 89
    const val versionName = "1.5.59"
    const val applicationId = "org.merakilearn"
}

object Classpath {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"

    // Newer versions may be available: please check here:
    // https://chaquo.com/chaquopy/doc/current/changelog.html
    const val python = "com.chaquo.python:gradle:${Versions.python}"
    const val realm = "io.realm:realm-gradle-plugin:${Versions.realm2}"
    const val googleServices = "com.google.gms:google-services:${Versions.googleServices}"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseCrashlytics}"
    const val firebasePerf =
        "com.google.firebase.firebase-perf:com.google.firebase.firebase-perf.gradle.plugin:${Versions.firebasePerf}"
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
}