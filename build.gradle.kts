plugins {
    id("java")
    id("war") // <-- Gretty 대신 war 플러그인 사용
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // 테스트 의존성은 그대로 둡니다
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 게임에 필요한 라이브러리
    implementation("com.google.code.gson:gson:2.10.1")

    // 서블릿 API는 빌드 시에만 필요하므로 'compileOnly'로 지정
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
}

tasks.test {
    useJUnitPlatform()
}