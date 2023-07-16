package dev.astralchroma.aggregator.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TargetConfiguration(@SerialName("_id") val targetChannel: Long, val sourceChannels: MutableList<Long>, val server: Long) {
	val toExport: TargetConfigurationExport
		get() = TargetConfigurationExport(targetChannel, sourceChannels, server)
}

@Serializable
data class TargetConfigurationExport(val targetChannel: Long, val sourceChannels: MutableList<Long>, val server: Long) {
	val toDatabase: TargetConfiguration
		get() = TargetConfiguration(targetChannel, sourceChannels, server)
}
