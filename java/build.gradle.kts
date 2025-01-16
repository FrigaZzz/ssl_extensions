plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.7.20"
  id("org.jetbrains.intellij") version "1.13.1"
}

sourceSets {
  main {
    java {
      srcDirs("src/main/java")
    }
    kotlin {
      srcDirs("src/main/kotlin")
    }
    resources {
      srcDirs("src/main/resources")
    }
  }
  test {
    java {
      srcDirs("src/test/java")
    }
    resources {
      srcDirs("src/test/resources")
    }
  }
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  // Add OkHttp dependency
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  
  // Add test dependencies
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2022.2.4")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf(
    "com.intellij.java"
  ))
}

tasks {
  processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // or DuplicatesStrategy.INCLUDE
  }

  processTestResources {
           duplicatesStrategy = DuplicatesStrategy.EXCLUDE // or DuplicatesStrategy.INCLUDE
       }

  // Set the JVM compatibility versions
  withType<JavaCompile> {
           sourceCompatibility = "17"
           targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
           kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("222")
    untilBuild.set("232.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }

  test {
    useJUnitPlatform()
  }
}
