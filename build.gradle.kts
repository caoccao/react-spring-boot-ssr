import org.gradle.internal.os.OperatingSystem

plugins {
	java
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.caoccao.javet.demo.react.ssr"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	val os = OperatingSystem.current()
	val arch = System.getProperty("os.arch")
	val osType = if (os.isWindows) "windows" else
		if (os.isMacOsX) "macos" else
			if (os.isLinux) "linux" else ""
	val archType = if (arch == "aarch64" || arch == "arm64") "arm64" else "x86_64"
	implementation("com.caoccao.javet:swc4j:1.2.0")
	implementation("com.caoccao.javet:javet:4.1.0")
	implementation("com.caoccao.javet:javet-node-$osType-$archType-i18n:4.1.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
