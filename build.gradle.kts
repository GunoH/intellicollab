buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${BuildConfig.kotlinVersion}")
    }
}

plugins {
    kotlin("jvm") version BuildConfig.kotlinVersion
    id("org.jetbrains.intellij") version BuildConfig.gradleIntellijPluginVersion
}

intellij {
    pluginName = "IntelliCollab"
    version = BuildConfig.intellijVersion
    updateSinceUntilBuild = false
}

group = "nl.guno"
version = BuildConfig.pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    compile(fileTree("lib"))
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.2.30")
    compile("org.ow2.asm:asm:5.0.4")
    compile("commons-beanutils:commons-beanutils:1.9.2")
    compile("org.apache.commons:commons-exec:1.2")
    compile("commons-io:commons-io:1.4")
    compile("com.fasterxml.jackson.core:jackson-annotations:2.5.0")
    compile("com.fasterxml.jackson.core:jackson-core:2.5.5")
    compile("com.fasterxml.jackson.core:jackson-databind:2.5.5")
    compile("com.fasterxml.jackson.module:jackson-module-mrbean:2.5.5")
    compile("com.google.guava:guava:18.0")
    compile("com.google.gwt:gwt-user:2.7.0")
    runtime("javax.xml.ws:jaxws-api:2.3.1")
}
