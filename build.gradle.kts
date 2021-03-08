
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

var kotlinVersion = "1.4.31"
var micronautVersion = "2.3.2"

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("kapt") version "1.4.31"
    //kotlin("allopen") version "1.4.31"
    //id("org.jetbrains.kotlin.plugin.allopen") version "1.4.10"
    //id("com.github.johnrengelman.shadow") version "5.0.0"
    id("application")
    id("org.mikeneck.graalvm-native-image") version "1.2.0"
    "java"
}

group = "sh.hutch"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
    implementation("io.micronaut:micronaut-runtime:$micronautVersion")
    implementation("io.micronaut:micronaut-http-server-netty:$micronautVersion")
    implementation("io.micronaut:micronaut-http-client:$micronautVersion")
    implementation("org.jsoup:jsoup:1.13.1")
    kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kapt("io.micronaut:micronaut-inject-java:$micronautVersion")
    kapt("io.micronaut:micronaut-validation:$micronautVersion")
    kapt("io.micronaut.configuration:micronaut-openapi:1.5.3")
    kaptTest(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kaptTest("io.micronaut:micronaut-inject-java:$micronautVersion")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("ch.qos.logback:logback-classic")
    
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("io.micronaut.test:micronaut-test-junit5:$micronautVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


/*nativeImage {
    graalVmHome = System.getenv("JAVA_HOME")
    mainClass ="sh.hutch.weather.Application"
    executableName = "weather"
    outputDirectory = file("$buildDir/executable")
    arguments(
        "--no-fallback",
        "--enable-all-security-services",
        //options.traceClassInitialization('com.example.MyDataProvider,com.example.MyDataConsumer'),
        "--initialize-at-run-time=com.example.runtime",
        "--report-unsupported-elements-at-runtime"
    )
}*/

generateNativeImageConfig {
    enabled = true
    byRunningApplicationWithoutArguments()
}
