import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("fabric-loom")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}

val modVersion: String by project
version = modVersion

val mavenGroup: String by project
group = mavenGroup

val loaderVersion: String by project
val fabricKotlinVersion: String by project

repositories {
    maven("https://jitpack.io")

    maven {
        name = "Modrinth"
        setUrl("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {

    val minecraftVersion: String by project
    minecraft("com.mojang", "minecraft", minecraftVersion)

    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")

    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)

    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)

    modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)

    val blueMapApiVersion: String by project
    compileOnly("com.github.BlueMap-Minecraft", "BlueMapAPI", blueMapApiVersion)
    modCompileOnlyApi("maven.modrinth", "bluemap", "5.14-fabric")

    val duckyUpdaterVersion: String by project
    include(modImplementation("maven.modrinth", "ducky-updater-lib", duckyUpdaterVersion))

    include(modImplementation("maven.modrinth", "fstats", "2026.1.1"))

    include(implementation("com.github.usefulness", "webp-imageio", "0.10.2"))

    // Native Integrations
    modCompileOnlyApi("maven.modrinth", "skinrestorer", "2.6.0+1.21.11-fabric")
}

tasks {
    val javaVersion = JavaVersion.VERSION_21

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

    jar {
        from("LICENSE")
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version,
                    "loaderVersion" to loaderVersion,
                    "fabricKotlinVersion" to fabricKotlinVersion,
                    "java" to javaVersion.toString()
                )
            )
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
