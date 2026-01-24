
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.gradleup.shadow") version "9.2.2"
  id("io.ebean") version "14.6.0"
}

// Ebean debug
//ebean {
//  debugLevel = 1
//}
ebean {
  debugLevel = 9
}

group = "com.example.starter"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.6"
val junitJupiterVersion = "5.9.1"
val mainVerticleName = "com.example.starter.MainVerticle"
val launcherClassName = "io.vertx.Launcher.application.VertxApplication"

application {
  mainClass.set("io.vertx.core.Launcher")
}

dependencies {
  // Vert.x
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-mysql-client")

  // Ebean ORM
  implementation("io.ebean:ebean:14.6.0")
  implementation("io.ebean:ebean-ddl-generator:14.6.0")

  // MySQL JDBC
  implementation("mysql:mysql-connector-java:8.0.33")
  implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

  // Logging
  implementation("org.slf4j:slf4j-simple:2.0.9")

  // MacOS Netty DNS
//  runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.2.9.Final")
  implementation("io.netty:netty-resolver-dns-native-macos:4.1.108.Final:osx-aarch_64")

  //JWT token
  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
  implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

  //Bcrypt
  implementation("org.mindrot:jbcrypt:0.4")

  // Testing
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")


}


java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}


// Use Vert.x launcher args
tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName)
}

// Test config
tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}


