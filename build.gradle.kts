import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    kotlin("kapt") version "1.3.21"
    application
    idea
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.micronaut:micronaut-bom:1.0.4")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation(kotlin("reflect"))
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-views")
    implementation("io.micronaut:micronaut-http-client")

    kapt("io.micronaut:micronaut-inject-java")

    runtime("org.thymeleaf:thymeleaf:3.0.11.RELEASE")
    runtime("org.webjars:bootstrap:4.3.1")
    runtime("ch.qos.logback:logback-classic:1.2.3")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
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