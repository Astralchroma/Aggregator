@Suppress("DSL_SCOPE_VIOLATION") // TODO: https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
	application
}

version = "1.1.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.jda) { exclude("opus-java") }
	implementation(libs.mongo)
}

kotlin.jvmToolchain(17)

application {
	mainClass.set("io.github.petercrawley.aggregator.Aggregator")
}
