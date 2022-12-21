package io.github.petercrawley.aggregator.database

import org.bson.codecs.pojo.annotations.BsonId

data class TargetConfiguration(@BsonId val targetChannel: Long, val sourceChannels: MutableList<Long>, val server: Long)
