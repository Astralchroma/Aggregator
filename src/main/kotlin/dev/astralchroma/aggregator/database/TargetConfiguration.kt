package dev.astralchroma.aggregator.database

import kotlinx.serialization.Serializable

@Serializable
data class TargetConfiguration(val _id: Long, val sourceChannels: MutableList<Long>, val server: Long) {
	fun toExport() = TargetConfigurationExport(_id, sourceChannels, server)
}

@Serializable
data class TargetConfigurationExport(val targetChannel: Long, val sourceChannels: MutableList<Long>, val server: Long) {
	fun toDatabase() = TargetConfiguration(targetChannel, sourceChannels, server)
}
