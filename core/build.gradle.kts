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

android {
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    publishing {
        singleVariant("release")
    }
    namespace = "knf.hydra.core"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    api("com.github.jordyamc:Cloudflare-Bypasser:1.0.26")
    //api "knf.tools:bypass:1.0.21"
    api("androidx.paging:paging-runtime-ktx:3.3.6")
    api("androidx.room:room-runtime:2.7.2")
    api("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")
    api("androidx.preference:preference-ktx:1.2.1")
    api("androidx.annotation:annotation:1.9.1")
    api("com.google.code.gson:gson:2.13.1")
    api("com.github.jordyamc:Gson-ktx:1.0")
    api("com.github.jordyamc.oasis-jsbridge-android:oasis-jsbridge-quickjs:1.0.2")
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:2.0.0")
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaGeneratePublicationJavadoc)
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

dokka {
    moduleName.set("Hydra core")
    dokkaSourceSets.main {
        includes.from("${rootProject.projectDir}/core/README.md")
        enableAndroidDocumentationLink = false
        suppressGeneratedFiles = true
        sourceLink {
            localDirectory.set(file("src/main/java"))
            remoteUrl("https://github.com/hydra-app/core/tree/master/core/src/main/java")
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        customAssets.from("${rootProject.projectDir}/logo-icon.svg")
        footerMessage = "© 2021-2025 Copyright KNF Apps"
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
    from ("${rootProject.projectDir}")
    into ("${rootProject.projectDir}/docs/images")
    include("logo-icon.svg")
}

afterEvaluate {
    //tasks["dokkaHtml"].dependsOn(tasks.getByName("generateReleaseRFile"), tasks.getByName("generateDebugRFile"))
    publishing {
        repositories {
            maven {
                url = uri(layout.buildDirectory.dir("repository"))
            }
        }
        publications {
            create<MavenPublication>("release") {
                groupId = "knf.hydra"
                artifactId = "core"
                version = "1.0.2-rc7"
                artifact(tasks.named("androidSourcesJar"))
                artifact(tasks.named("dokkaHtmlJar"))
                artifact(tasks.named("dokkaJavadocJar"))
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
                afterEvaluate {
                    from(components.findByName("release"))
                }
            }
        }
    }
    tasks.named("publishToMavenLocal").configure {
        finalizedBy(tasks.named("copyLogo"))
    }
}