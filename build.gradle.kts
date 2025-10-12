plugins {
    // 기본 Java 플러그인
    id("java")
    // 웹 애플리케이션(.war) 빌드를 위한 플러그인
    id("war")
    // javax.servlet API와 호환되는 안정적인 버전
    id("org.gretty") version "3.1.1"
}

// 프로젝트의 그룹명과 버전 설정
group = "org.example"
version = "1.0-SNAPSHOT"

// 라이브러리를 다운로드할 원격 저장소 설정
repositories {
    mavenCentral()
}

// 프로젝트에서 사용할 라이브러리(의존성) 목록
dependencies {
    // --- 웹 애플리케이션 필수 라이브러리 ---
    // Servlet API: 컴파일 시에만 필요하며, 실제 서버(Tomcat/Gretty)에 이미 포함되어 있음
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    // Gson: Java 객체와 JSON 데이터 변환 라이브러리
    implementation("com.google.code.gson:gson:2.10.1")

    // --- 테스트용 라이브러리 ---
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// ⭐️ 최종 수정: 프로젝트의 Java 버전을 Gretty 서버와 호환되는 17 버전으로 변경합니다.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Java 소스 코드를 컴파일할 때 인코딩을 UTF-8로 설정 (한글 깨짐 방지)
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// .war 파일을 생성할 때, 웹 리소스(JSP, HTML 등)가 있는 폴더의 위치를 명시
tasks.war {
    webAppDirectory.set(file("src/main/webapp"))
}

// JUnit 5 버전으로 테스트를 실행하도록 설정
tasks.test {
    useJUnitPlatform()
}

// Gretty 서버가 사용할 포트를 9090번으로 변경합니다.
gretty {
    httpPort = 9090
}

