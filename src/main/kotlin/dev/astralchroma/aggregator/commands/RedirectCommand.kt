package dev.astralchroma.aggregator.commands

import dev.astralchroma.aggregator.Aggregator
import dev.astralchroma.aggregator.CommandClass
import dev.astralchroma.aggregator.annotations.Command
import dev.astralchroma.aggregator.annotations.EnabledFor
import dev.astralchroma.aggregator.annotations.Parameter
import dev.astralchroma.aggregator.annotations.Subcommand
import dev.astralchroma.aggregator.database.TargetConfiguration
import dev.astralchroma.aggregator.messageEmbed
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.updateOne

@EnabledFor(Permission.MANAGE_CHANNEL)
@Command("redirect", "Configures a channels redirection target.", guildOnly = true)
class RedirectCommand : CommandClass() {
	@Suppress("Unused")
	@Subcommand("set", "Sets a channel's target channel.")
	fun onRedirectSetCommand(
		event: SlashCommandInteractionEvent,
		@Parameter("channel", "Channel", ChannelType.TEXT) channel: TextChannel,
		@Parameter("target", "Target Channel", ChannelType.TEXT) target: TextChannel
	) {
		val targetConfiguration = Aggregator.targetConfiguration.findOne(TargetConfiguration::targetChannel eq target.idLong)

		when (targetConfiguration == null) {
			true -> Aggregator.targetConfiguration.insertOne(TargetConfiguration(target.idLong, mutableListOf(channel.idLong), event.guild!!.idLong))
			false -> {
				targetConfiguration.sourceChannels.add(channel.idLong)
				Aggregator.targetConfiguration.updateOne(targetConfiguration)
			}
		}

		event.reply("Created forwarding configuration ${channel.asMention} > ${target.asMention}.").queue()
	}

	@Suppress("Unused")
	@Subcommand("unset", "Unsets a channel's target channel.")
	fun onRedirectUnsetCommand(
		event: SlashCommandInteractionEvent,
		@Parameter("channel", "Channel", ChannelType.TEXT) channel: TextChannel,
		@Parameter("target", "Target Channel", ChannelType.TEXT) target: TextChannel
	) {
		val targetConfiguration = Aggregator.targetConfiguration.findOne(TargetConfiguration::targetChannel eq target.idLong)

		if (targetConfiguration == null) {
			event.reply("No forwarding configuration exists for ${target.asMention}.").queue()
			return
		}

		when (targetConfiguration.sourceChannels.size == 1) {
			true -> Aggregator.targetConfiguration.deleteOne(TargetConfiguration::targetChannel eq target.idLong)
			false -> {
				targetConfiguration.sourceChannels.remove(channel.idLong)
				Aggregator.targetConfiguration.updateOne(targetConfiguration)
			}
		}

		event.reply("Removed forwarding configuration ${channel.asMention} > ${target.asMention}.").queue()
	}

	@Suppress("Unused")
	@Subcommand("list", "Lists this server's forwarding rules.")
	fun onRedirectListCommand(event: SlashCommandInteractionEvent) {
		val targetConfigurations = Aggregator.targetConfiguration.find(TargetConfiguration::server eq event.guild!!.idLong)
		val fields = mutableListOf<MessageEmbed.Field>()

		for (targetConfiguration in targetConfigurations) {
			fields.add(
				MessageEmbed.Field(
					"**Target:**",
					targetConfiguration.sourceChannels.joinToString("\n", "<#${targetConfiguration.targetChannel}>\n**Sources:**\n") { "<#$it>" },
				true
				)
			)
		}

		event.replyEmbeds(messageEmbed(
			title = "Forwarding configuration for ${event.guild!!.name}",
			fields = fields
		)).queue()
	}
}
