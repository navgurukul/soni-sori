// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = uri("https://plugins.gradle.org/m2/"))
        maven(url = uri("https://chaquo.com/maven"))
    }
    dependencies {
        classpath(Classpath.gradle)
        classpath(Classpath.kotlin)
        classpath(Classpath.python)
        classpath(Classpath.googleServices)
        classpath(Classpath.firebaseCrashlytics)
        classpath(Classpath.realm)
        classpath(Classpath.firebasePerf)
        classpath(Classpath.downloadTask)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = uri("https://maven.aliyun.com/repository/jcenter"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://jitpack.io")) {
            content {
                includeGroup("com.github.amulyakhare")
                includeGroup("com.github.tapadoo")
                includeGroup("com.github.Zhuinden")
                includeGroup("com.github.amulyakhare")
                includeGroup("com.github.chrisbanes")
                includeGroup("com.github.GrenderG")
                includeGroup("org.matrix.gitlab.matrix-org")
                includeGroupByRegex("com\\.github\\..*")
            }
        }
        // Some older libraries might still be in these locations
        maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        // Adding JCenter as a fallback for old dependencies, despite its status
        @Suppress("DEPRECATION")
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
