val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposedVersion: String by project
val ktormVersion: String by project
val ktor_version2: String by project
val jbcrypt_version: String by project
val simpleKotlinMail: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
                id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version2")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version2")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version2")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version2")

    //Gson
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version2")

    //HttpClient
    implementation("io.ktor:ktor-client-cio:$ktor_version2")


    //Authentication
    implementation("io.ktor:ktor-server-auth:$ktor_version2")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version2")

    implementation ("org.mindrot:jbcrypt:$jbcrypt_version")

    //database
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-support-mysql:$ktormVersion")

    //email sending
    //implementation("org.apache.commons:commons-email:1.5")
    //SimpleKotlinMail
    implementation("net.axay:simplekotlinmail-core:$simpleKotlinMail")
    implementation("net.axay:simplekotlinmail-client:$simpleKotlinMail")

}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}