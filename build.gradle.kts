import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val archivesBaseName: String by project
val mavenGroup: String by project
val modVersion: String by project

val javaVersion = JavaVersion.VERSION_21

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

base {
    archivesName.set(archivesBaseName)
}

group = mavenGroup
version = modVersion

repositories {
    maven("https://repo.bluecolored.de/releases")
    maven {
        name = "Modrinth"
        setUrl("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {

    minecraft(libs.minecraft)

    implementation(libs.fabric.api)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.language.kotlin)

    compileOnly(libs.bluemap)
    compileOnlyApi(libs.bluemap.api)

    embed(libs.ducky.updater)
    embed(libs.fstats)
    embed(libs.webp.imageio)

    // Native Integrations
    compileOnlyApi(libs.skin.restorer)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withSourcesJar()
}

tasks {
    jar {
        from("LICENSE")
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
        }
    }
}

fun DependencyHandlerScope.embed(projectDependency: Provider<MinimalExternalModuleDependency>) {
    implementation(projectDependency)
    include(projectDependency)
}