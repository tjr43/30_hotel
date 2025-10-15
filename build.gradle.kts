plugins {
    java
    war
    id("org.gretty") version "3.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.war {
    webAppDirectory.set(file("src/main/webapp"))
}

tasks.test {
    useJUnitPlatform()
}

gretty {
    httpPort = 9090
    servletContainer = "tomcat9"
    contextPath = "/"
}

