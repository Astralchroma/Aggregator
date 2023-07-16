package dev.astralchroma.aggregator.data

import dev.astralchroma.aggregator.database.TargetConfigurationExport
import kotlinx.serialization.Serializable

@Serializable
data class Data(
	val servers: Array<TargetConfigurationExport> = arrayOf(),
) {
	// Auto generated IntelliJ because it asked me too
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Data

		if (!servers.contentEquals(other.servers)) return false

		return true
	}

	override fun hashCode(): Int = servers.contentHashCode()
}
