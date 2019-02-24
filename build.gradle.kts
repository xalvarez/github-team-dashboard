plugins {
    kotlin("jvm") version "1.3.21"
    application
    idea
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

application {
    mainClassName = "com.github.xalvarez.githubteamdashboard.GitHubTeamDashboardApplication"
}
