plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ksp)
    alias(libs.plugins.aboutlibraries)
}

setupApp()

val buildHevSocks5Tunnel by tasks.registering {
    group = "build"
    description = "Build hev-socks5-tunnel native library with ndk-build"

    val srcDir = file("../library/hev-tunnel")
    val hevSrcDir = file("../library/hev-tunnel/hev-socks5-tunnel")
    val outDir = file("src/main/jniLibs")

    inputs.dir(file("$hevSrcDir/src"))
    outputs.dir(file("$hevSrcDir/libs"))

    doLast {
        val ndkBuild = if (System.getProperty("os.name").startsWith("Windows")) "ndk-build.cmd" else "ndk-build"
        val ndkPath = System.getenv("ANDROID_NDK_HOME")
        val ndk = "$ndkPath/$ndkBuild"

        exec {
            workingDir = srcDir
            commandLine(ndk, "-j${Runtime.getRuntime().availableProcessors()}", "NDK_PROJECT_PATH=.", "APP_BUILD_SCRIPT=Android.mk")
        }

        copy {
            from("$hevSrcDir/libs") {
                include("**/*.so")
            }
            into(outDir)
        }
    }
}

tasks.matching { it.name.startsWith("merge") && it.name.endsWith("JniLibFolders") }.configureEach {
    dependsOn(buildHevSocks5Tunnel)
}

android {
    namespace = "io.nekohasekai.sagernet"
}

ksp {
    arg("room.incremental", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

aboutLibraries {
    offlineMode = true
    collect {
        configPath = file("src/main/aboutlibraries")
        includePlatform = true
    }
    export {
        outputFile = file("src/main/res/raw/aboutlibraries.json")
        excludeFields.addAll("name", "description", "developers", "funding", "licenses", "organization", "scm", "website", "License")
        prettyPrint = true
    }
}

dependencies {
    implementation(fileTree("libs"))
    implementation(project(":library:proto-stub"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.camera2)
    implementation(libs.swiperefreshlayout)
    implementation(libs.appcompat)
    implementation(libs.preference)
    implementation(libs.flexbox)
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.multiprocess)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.zxing.core)
    implementation(libs.snakeyaml)
    implementation(libs.material.about.library)
    implementation(libs.process.phoenix)
    implementation(libs.kryo)
    implementation(libs.jini.lib)
    implementation(libs.markwon.core)
    implementation(libs.recyclerview.fastscroll) {
        exclude(group = "androidx.recyclerview")
        exclude(group = "androidx.appcompat")
    }
    implementation(libs.editorkit)
    implementation(libs.editorkit.language.json)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
