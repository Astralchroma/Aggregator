package dev.astralchroma.aggregator

import club.minnced.discord.webhook.external.JDAWebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import dev.astralchroma.aggregator.commands.DataCommand
import dev.astralchroma.aggregator.commands.HelpCommand
import dev.astralchroma.aggregator.commands.RedirectCommand
import dev.astralchroma.aggregator.commands.UptimeCommand
import dev.astralchroma.aggregator.database.TargetConfiguration
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import org.litote.kmongo.KMongo.createClient
import org.litote.kmongo.contains
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.getCollection
import kotlin.system.exitProcess

object Aggregator : ListenerAdapter() {
	val startTime = System.currentTimeMillis()

	private fun getVariable(name: String): String {
		val variable = System.getenv()[name]

		if (variable == null) {
			System.err.println("Environment variable \"$name\" is missing.")
			exitProcess(1)
		}

		return variable
	}

	private val token = getVariable("DISCORD_TOKEN")
	private val connectionString = getVariable("MONGO_URI")
	private val databaseName = getVariable("MONGO_DATABASE")
	val botOwnerSnowflake = System.getenv()["OWNER_SNOWFLAKE"]

	private val jda = JDABuilder.createLight(token)
		.setEnabledIntents(listOf(
			GatewayIntent.MESSAGE_CONTENT,
			GatewayIntent.GUILD_MESSAGES
		))
		.addEventListeners(this)
		.build()

	private val executionDataMap = mutableMapOf<String, (SlashCommandInteractionEvent) -> Unit>()

	private val mongo = createClient(
		MongoClientSettings
			.builder()
			.applyConnectionString(ConnectionString(connectionString))
			.build()
	)

	private val database = mongo.getDatabase(databaseName)

	val targetConfiguration = database.getCollection<TargetConfiguration>().apply {
		ensureIndex(TargetConfiguration::server)
	}

	@JvmStatic
	fun main(vararg arguments: String) {
		// JDA Command Initialisation
		val commandDataList = mutableListOf<SlashCommandData>()

		fun registerCommand(commandClass: CommandClass) {
			val (commandData, executionData) = commandClass.constructCommandData()
			executionDataMap.putAll(executionData)
			commandDataList.add(commandData)
		}

		registerCommand(DataCommand())
		registerCommand(HelpCommand())
		registerCommand(RedirectCommand())
		registerCommand(UptimeCommand())

		jda.updateCommands().addCommands(commandDataList).queue()

		// Ensure clean shutdowns
		Runtime.getRuntime().addShutdownHook(Thread { stop() })
	}

	private fun stop() {
		jda.shutdown()
		mongo.close()
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (!event.message.flags.contains(Message.MessageFlag.IS_CROSSPOST)) return // Only allow "crosspost" messages.

		val configurations = targetConfiguration.find(TargetConfiguration::sourceChannels contains event.channel.idLong)

		for (configuration in configurations) {
			val targetChannel = event.guild.getTextChannelById(configuration.targetChannel) ?: continue

			val webhooks = targetChannel.retrieveWebhooks().complete()
			val webhook = webhooks.find { it.ownerAsUser?.idLong == jda.selfUser.idLong } ?: targetChannel.createWebhook("Aggregator Target").complete()
			val webhookClient = JDAWebhookClient.from(webhook)

			val message = WebhookMessageBuilder()
				.setUsername(event.author.name)
				.setAvatarUrl(event.author.avatarUrl)
				.setContent(event.message.contentRaw)
				.apply {
					event.message.attachments.forEach { addFile(it.fileName, it.proxy.download().join()) }
					event.message.embeds.forEach {
						addEmbeds(WebhookEmbed(
							it.timestamp,
							it.colorRaw,
							it.description,
							it.thumbnail?.url,
							it.image?.url,
							it.footer?.text?.let { it1 -> WebhookEmbed.EmbedFooter(it1, it.footer?.iconUrl) },
							it.title?.let { it1 -> WebhookEmbed.EmbedTitle(it1, it.url) },
							it.author?.name?.let { it1 -> WebhookEmbed.EmbedAuthor(it1, it.author?.iconUrl, it.author?.url) },
							it.fields.map { it1 -> WebhookEmbed.EmbedField(it1.isInline, it1.name ?: "", it1.value ?: "") }
						))
					}
				}
				.build()

			webhookClient.send(message)
		}
	}

	override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
		executionDataMap[event.fullCommandName]!!.invoke(event)
	}
}
