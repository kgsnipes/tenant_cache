plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.dws"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/org.ehcache/ehcache
    implementation("org.ehcache:ehcache:3.10.8")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j:slf4j-simple:2.0.13")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.20")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}