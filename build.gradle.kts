plugins {
    id("java")
    id("war")
    id("org.gretty") version "4.1.3" // 이 라인을 추가하세요
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 게임에 필요한 라이브러리
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    // webapp 폴더의 위치를 정확히 지정합니다.
    webAppDirectory.set(file("src/main/webapp"))
}