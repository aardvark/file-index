plugins {
    java
}

group = "net.fiendishplatypus.utils"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.slf4j", "slf4j-api", "1.7.28")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}