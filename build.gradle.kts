plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.vincenzo.spring"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {

//	implementation("org.springframework.boot:spring-boot-starter-web") // 기존의 Spring MVC 의존성은 gateway의 webflux와 맞지 않아서 충돌이 난다

	implementation("org.springframework.cloud:spring-boot-starter-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
//	implementation("org.springframework.cloud:spring-cloud-starter-gateway-mvc")

//	implementation("io.netty:netty-resolver-dns-native-macos:${nettyVersion}:osx-aarch_64")
	implementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")



	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
