@Suppress("DSL_SCOPE_VIOLATION") // TODO: https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
	alias(libs.plugins.serialization)
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
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
}

kotlin.jvmToolchain(17)

application {
	mainClass.set("dev.astralchroma.aggregator.Aggregator")
}
