package io.github.petercrawley.aggregator.commands

import io.github.petercrawley.aggregator.Aggregator
import io.github.petercrawley.aggregator.CommandClass
import io.github.petercrawley.aggregator.annotations.Command
import io.github.petercrawley.aggregator.annotations.Default
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Command("uptime", "Returns Aggregator's uptime.")
class UptimeCommand : CommandClass() {
	@Default
	@Suppress("Unused")
	fun onUptimeCommand(event: SlashCommandInteractionEvent) {
		event.reply("Aggregator was started <t:${Aggregator.startTime / 1000}:R>.").queue()
	}
}
