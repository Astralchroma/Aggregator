plugins {
	@Suppress("DSL_SCOPE_VIOLATION") // TODO: https://youtrack.jetbrains.com/issue/KTIJ-19369
	alias(libs.plugins.kotlin)
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.jda) { exclude("opus-java") }
	implementation(libs.mongo)
}

kotlin.jvmToolchain(17)
