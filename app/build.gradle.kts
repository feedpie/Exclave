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
    description = "Download pre-built hev-socks5-tunnel native library"

    val jniLibsDir = file("src/main/jniLibs")
    val hevVersion = "2.15.0"
    val abis = mapOf(
        "arm64-v8a" to "hev-socks5-tunnel-linux-arm64",
        "armeabi-v7a" to "hev-socks5-tunnel-linux-arm32v7",
        "x86" to "hev-socks5-tunnel-linux-i686",
        "x86_64" to "hev-socks5-tunnel-linux-x86_64",
    )

    doLast {
        abis.forEach { (abi, releaseName) ->
            val targetDir = file("$jniLibsDir/$abi")
            val targetFile = file("$targetDir/libhev-socks5-tunnel.so")
            if (targetFile.exists() && targetFile.length() > 0) return@forEach
            targetDir.mkdirs()
            val url = "https://github.com/heiher/hev-socks5-tunnel/releases/download/$hevVersion/$releaseName"
            logger.lifecycle("Downloading $url -> $targetFile")
            val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 120000
            conn.instanceFollowRedirects = true
            conn.requestMethod = "GET"
            try {
                val input = conn.inputStream
                val output = java.io.FileOutputStream(targetFile)
                val buf = ByteArray(8192)
                var read: Int
                while (input.read(buf).also { read = it } != -1) {
                    output.write(buf, 0, read)
                }
                output.close()
                input.close()
            } finally {
                conn.disconnect()
            }
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
