
import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Calendar

/*
 * Created by @UnbarredStream on 30/07/22 13:36
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 30/07/22 13:12
 */

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("org.jetbrains.dokka")
    id("org.jetbrains.dokka-javadoc")
}

extensions.configure<LibraryExtension> {
    compileSdk = 36
    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles.add(file("consumer-rules.pro"))
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    namespace = "knf.hydra.core"
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.10")
    api("com.github.jordyamc:Cloudflare-Bypasser:1.0.26")
    //api "knf.tools:bypass:1.0.21"
    api("androidx.paging:paging-runtime-ktx:3.5.0")
    api("androidx.room:room-runtime:2.8.4")
    api("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    api("androidx.preference:preference-ktx:1.2.1")
    api("androidx.annotation:annotation:1.10.0")
    api("com.squareup.okhttp3:okhttp:5.4.0")
    api("com.squareup.okhttp3:okhttp-dnsoverhttps:5.4.0")
    api("com.google.code.gson:gson:2.14.0")
    api("com.github.jordyamc:Gson-ktx:1.0")
    api("com.github.jordyamc.oasis-jsbridge-android:oasis-jsbridge-quickjs:1.0.2")
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:2.2.0")
}

tasks.register<Jar>("dokkaHtmlJar") {
    description = "Generate Html for Dokka documentation"
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    description = "Generate Jardov for Dokka documentation"
    dependsOn(tasks.dokkaGeneratePublicationJavadoc)
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

dokka {
    moduleName.set("Hydra core")
    dokkaSourceSets.debug {
        suppress.set(true)
    }
    dokkaSourceSets.release {
        suppress.set(true)
    }
    dokkaSourceSets.main {
        includes.from("${rootDir}/core/README.md")
        enableAndroidDocumentationLink.set(false)
        sourceLink {
            localDirectory.set(file("${rootDir}/core/src"))
            remoteUrl("https://github.com/hydra-app/core/tree/master/core/src")
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        customAssets.from("${rootDir}/logo-icon.svg")
        footerMessage = "© 2021-${Calendar.getInstance().get(Calendar.YEAR)} Copyright KNF Apps"
    }
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        outputDirectory.set(layout.buildDirectory.dir("$rootDir/docs"))
    }
    dokkaPublications.javadoc {
        suppressInheritedMembers.set(true)
        outputDirectory.set(layout.buildDirectory.dir("$rootDir/javadoc"))
    }
}

tasks.register<Copy>("copyLogo") {
    description = "Replace html logo"
    from ("${rootProject.projectDir}")
    into ("${rootProject.projectDir}/docs/images")
    include("logo-icon.svg")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "knf.hydra"
            artifactId = "core"
            version = "1.0.2-rc9"
            afterEvaluate {
                from(components["release"])
                artifact(tasks.named("dokkaHtmlJar"))
                artifact(tasks.named("dokkaJavadocJar"))
                tasks.named("publishToMavenLocal").configure {
                    finalizedBy(tasks.named("copyLogo"))
                }
            }
            pom {
                name.set("Hydra Core Library")
                description.set("Core library for Hydra modules.")
                url.set("https://knf-hydra.app")
                developers {
                    developer {
                        id.set("UnbarredStream")
                        name.set("KNF Apps")
                        email.set("hydra.dev@hotmail.com")
                    }
                }
            }
        }
    }
}