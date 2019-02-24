plugins {
    kotlin("jvm") version "1.3.21"
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
    implementation(kotlin("reflect"))
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-views")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

application {
    mainClassName = "com.github.xalvarez.githubteamdashboard.Application"
}
