import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("kapt") version "1.3.50"
    application
    idea
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.micronaut:micronaut-bom:1.2.0")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation(kotlin("reflect"))
    implementation(kotlin("allopen"))
    //org.jetbrains.kotlin:kotlin-allopen
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-views")
    implementation("io.micronaut:micronaut-http-client")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")

    kapt("io.micronaut:micronaut-inject-java")

    runtime("org.thymeleaf:thymeleaf:3.0.11.RELEASE")
    runtime("org.webjars:bootstrap:4.3.1")
    runtime("ch.qos.logback:logback-classic:1.2.3")

    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation(kotlin("test-junit5"))
    testImplementation("io.micronaut.test:micronaut-test-junit5:1.1.0")
    testImplementation("com.github.tomakehurst:wiremock:2.24.1")
    testImplementation("org.mockito:mockito-junit-jupiter:3.0.0")

    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.2")
}

application {
    mainClassName = "com.github.xalvarez.githubteamdashboard.Application"
}

val run by tasks.getting(JavaExec::class) {
    jvmArgs("-noverify", "-XX:TieredStopAtLevel=1")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
    }
}
val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
    }
}