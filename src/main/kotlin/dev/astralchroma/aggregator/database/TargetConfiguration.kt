package dev.astralchroma.aggregator.database

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class TargetConfiguration(@BsonId val targetChannel: Long, val sourceChannels: MutableList<Long>, val server: Long)
