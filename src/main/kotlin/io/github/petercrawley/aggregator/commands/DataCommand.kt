package io.github.petercrawley.aggregator.commands

import io.github.petercrawley.aggregator.Aggregator
import io.github.petercrawley.aggregator.CommandClass
import io.github.petercrawley.aggregator.annotations.Command
import io.github.petercrawley.aggregator.annotations.Parameter
import io.github.petercrawley.aggregator.annotations.Subcommand
import io.github.petercrawley.aggregator.data.Data
import io.github.petercrawley.aggregator.messageEmbed
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import org.litote.kmongo.findOneById
import kotlin.system.measureTimeMillis

@Command("data", "Import or export Aggregator data (Bot Owner Only)")
class DataCommand : CommandClass() {
	@Suppress("Unused")
	@Subcommand("export", "Exports all Aggregator data")
	fun onDataExportAllCommand(event: SlashCommandInteractionEvent) {
		if (event.user.id != Aggregator.botOwnerSnowflake) {
			event.replyEmbeds(messageEmbed(description = "You do not have permission to use this command."))
				.setEphemeral(true)
				.queue()
			return
		}

		val data = Data(Aggregator.targetConfiguration.find().toList().toTypedArray())

		event.replyFiles(FileUpload.fromData(Json.encodeToString(data).toByteArray(), "data.json"))
			.setEphemeral(true)
			.queue()
	}

	@Suppress("Unused")
	@OptIn(ExperimentalSerializationApi::class)
	@Subcommand("import", "Imports all Aggregator data")
	fun onDataImportAllCommand(event: SlashCommandInteractionEvent, @Parameter("data", "Data file exported from Aggregator") dataFile: Attachment) {
		if (event.user.id != Aggregator.botOwnerSnowflake) {
			event.replyEmbeds(messageEmbed(description = "You do not have permission to use this command."))
				.setEphemeral(true)
				.queue()
			return
		}

		dataFile.proxy.download().thenAcceptAsync {
			var response = ""

			val time = measureTimeMillis {
				val data = Json.decodeFromStream<Data>(it)

				for (configuration in data.servers) {
					if (Aggregator.targetConfiguration.findOneById(configuration.targetChannel) != null) {
						response += "- *${configuration.targetChannel} already exists.*\n"
						continue
					}

					Aggregator.targetConfiguration.insertOne(configuration)
					response += "- Added configuration for ${configuration.targetChannel}\n"
				}
			}

			response += "Done in ${time}ms."
			event.reply(response).setEphemeral(true).queue()
		}
	}
}
