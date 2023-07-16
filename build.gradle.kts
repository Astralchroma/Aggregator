@Suppress("DSL_SCOPE_VIOLATION") // TODO: https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
	alias(libs.plugins.serialization)
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
	alias(libs.plugins.native)
	application
}

version = "1.4.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.jda) { exclude("opus-java") }
	implementation(libs.mongo)
	implementation(libs.webhooks)
	implementation(libs.json)
	implementation(libs.slf4j)
	implementation(libs.logback)
	compileOnly(libs.bundles.native)
}

kotlin.jvmToolchain(17)

application {
	mainClass.set("dev.astralchroma.aggregator.Aggregator")
}

// Enjoy
graalvmNative {
	binaries {
		named("main") {
			buildArgs.addAll(
				"-H:+ReportExceptionStackTraces",
				"--trace-object-instantiation=java.security.SecureRandom,java.util.Random",
				"--trace-class-initialization=javax.xml.parsers.FactoryFinder",
				"--trace-class-initialization=jdk.xml.internal.SecuritySupport",
				"-J-Xmx2G",
			)
		}
	}
}
