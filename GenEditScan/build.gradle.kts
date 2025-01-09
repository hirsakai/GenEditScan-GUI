plugins {
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

sourceSets {
    main {
        java.srcDir("src/main/java")
        resources.srcDir("src/main/java")
    }
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // This dependency is used by the application.
    implementation("org.apache.xmlgraphics:batik-all:1.18")
    implementation("colt:colt:1.2.0")
    implementation("org.apache.xmlgraphics:fop:2.10")
    implementation(fileTree(mapOf("dir" to "libs/jfxConverter-0.24/distrib/svg", "include" to listOf("*.jar"))))
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    modules("javafx.controls", "javafx.fxml", "javafx.swing")
}

application {
    // Define the main class for the application.
    mainClass = "GenEditScan.Main"
}

tasks.named<Jar>("jar") {
    destinationDirectory.set(layout.projectDirectory)
}

tasks.register<Copy>("extractLibs") {
    dependsOn("distZip")

    val zipFile = layout.buildDirectory.file("distributions/${project.name}.zip")

    from(zipTree(zipFile)) {
        include("**/lib/*.jar")
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
        includeEmptyDirs = false
    }

    into("$projectDir/libs")
}

tasks.named("build") {
    finalizedBy("extractLibs")
    finalizedBy("clean")
}

//tasks {
//    withType<JavaCompile> {
//        options.compilerArgs.add("-Xlint:unchecked")
//    }
//}
