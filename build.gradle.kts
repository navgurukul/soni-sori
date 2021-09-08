// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        maven {
            url = uri("https://chaquo.com/maven")
        }
        maven {
            url = uri("http://dl.bintray.com/amulyakhare/maven")
        }
    }
    dependencies {
        classpath(Classpath.gradle)
        classpath(Classpath.kotlin)
        // Newer versions may be available: please check here:
        // https://chaquo.com/chaquopy/doc/current/changelog.html
        classpath(Classpath.python)
        classpath(Classpath.googleServices)
        classpath(Classpath.firebaseCrashlytics)
        classpath(Classpath.realm)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()

        // For olm library. This has to be declared first, to ensure that Olm library is not downloaded from another repo
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("http://dl.bintray.com/amulyakhare/maven")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}