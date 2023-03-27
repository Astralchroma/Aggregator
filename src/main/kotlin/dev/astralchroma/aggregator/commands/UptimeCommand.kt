package dev.astralchroma.aggregator.commands

import dev.astralchroma.aggregator.Aggregator
import dev.astralchroma.aggregator.CommandClass
import dev.astralchroma.aggregator.annotations.Command
import dev.astralchroma.aggregator.annotations.Default
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Command("uptime", "Returns Aggregator's uptime.")
class UptimeCommand : CommandClass() {
	@Default
	@Suppress("Unused")
	fun onUptimeCommand(event: SlashCommandInteractionEvent) {
		event.reply("Aggregator was started <t:${Aggregator.startTime / 1000}:R>.").queue()
	}
}
