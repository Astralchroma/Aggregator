package dev.astralchroma.aggregator.commands

import dev.astralchroma.aggregator.CommandClass
import dev.astralchroma.aggregator.messageEmbed
import dev.astralchroma.aggregator.annotations.Command
import dev.astralchroma.aggregator.annotations.Default
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Command("help", "Returns information about Aggregator.")
class HelpCommand : CommandClass() {
	@Default
	@Suppress("Unused")
	fun onHelpCommand(event: SlashCommandInteractionEvent) = event.replyEmbeds(messageEmbed(
		title = "Aggregator",
		description = """
Aggregator forwards messages from channels which follow announcement channels to a singular target channel in order to circumvent Discord's 15 webhook per channel limitation.

Using Aggregator is simple. Simply use </redirect set:1055163258102886490> and specify the channel and the target channel.

**Links:** [Bot Invite](https://discord.com/api/oauth2/authorize?client_id=1034850407450693714&permissions=536871936&scope=bot) / [Discord Server](https://discord.gg/kYFZtajTdx) / [GitHub](https://github.com/Peter-Crawley/Aggregator) / [Documentation](https://tinyurl.com/2dumcd5k)
		""".trimIndent(),
		color = 0x7f7fff
	)).queue()
}
