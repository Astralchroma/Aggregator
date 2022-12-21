package io.github.petercrawley.aggregator.annotations

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Target(CLASS)
annotation class Command(
	val name: String,
	val description: String,
	val guildOnly: Boolean = false
)

@Target(CLASS)
annotation class EnabledFor(
	vararg val permissions: Permission
)

@Target(FUNCTION)
annotation class Default

@Target(FUNCTION)
annotation class Subcommand(
	val name: String,
	val description: String
)

@Target(VALUE_PARAMETER)
annotation class Parameter(
	val name: String,
	val description: String,
	vararg val channelTypes: ChannelType = []
)
