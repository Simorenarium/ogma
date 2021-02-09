import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.4.10"
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    jacoco
}

group = "coffee.michel"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/dv8fromtheworld/maven")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:1.7.6")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // maybe later
    // implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.postgresql:postgresql:42.2.18")
    //TODO do I really need flyway?
    implementation("org.flywaydb:flyway-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("co.elastic.apm:apm-agent-api:1.17.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Business-Logic dependencies
    implementation("net.dv8tion:JDA:4.2.0_227")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.ninja-squad:springmockk:3.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    runtimeOnly("com.h2database:h2")
}

springBoot {
    buildInfo()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val coverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage."

    dependsOn("test", "jacocoTestReport", "jacocoTestCoverageVerification")
    val jacocoTestReport = tasks.findByName("jacocoTestReport")
    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter(jacocoTestReport)
}

val jacocoCoverage = "0.9".toBigDecimal()
val jacocoCounters = listOf(
    "LINE",
    "BRANCH",
    "METHOD"
)
val jacocoExcludes = listOf(
    "coffee/michel/ogma/Application.kt",
    "coffee/michel/ogma/ApplicationKt.class",
    "coffee/michel/ogma/context/*"
)

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        xml.destination = file("${buildDir}/jacoco.xml")
        csv.isEnabled = false
        html.isEnabled = false
    }
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExcludes)
        }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        jacocoCounters.map {
            rule {
                limit {
                    counter = it
                    value = "COVEREDRATIO"
                    minimum = jacocoCoverage
                }
            }
        }
    }
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExcludes)
        }
    )
}
