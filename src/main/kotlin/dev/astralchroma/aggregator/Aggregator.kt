package dev.astralchroma.aggregator

import club.minnced.discord.webhook.external.JDAWebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import dev.astralchroma.aggregator.commands.DataCommand
import dev.astralchroma.aggregator.commands.HelpCommand
import dev.astralchroma.aggregator.commands.RedirectCommand
import dev.astralchroma.aggregator.commands.UptimeCommand
import dev.astralchroma.aggregator.database.TargetConfiguration
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import org.litote.kmongo.KMongo.createClient
import org.litote.kmongo.contains
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.getCollection
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

object Aggregator : ListenerAdapter() {
	private val logger = LoggerFactory.getLogger(Aggregator::class.java)
	val startTime = System.currentTimeMillis()

	private fun getVariable(name: String): String {
		val variable = System.getenv(name)

		if (variable == null) {
			System.err.println("Environment variable \"$name\" is missing.")
			exitProcess(1)
		}

		return variable
	}

	var botOwnerSnowflake: Long = 0
		private set

	private lateinit var jda: JDA

	private val executionDataMap = mutableMapOf<String, (SlashCommandInteractionEvent) -> Unit>()
	private val commandDataList = ArrayList<SlashCommandData>()

	private lateinit var mongo: MongoClient
	private lateinit var database: MongoDatabase
	lateinit var targetConfiguration: MongoCollection<TargetConfiguration>
		private set

	private fun registerCommand(commandClass: CommandClass) {
		val (commandData, executionData) = commandClass.constructCommandData()
		executionDataMap.putAll(executionData)
		commandDataList.add(commandData)
		logger.info("Adding {} with {} -> {}", commandClass, commandData, executionData)
	}

	init {
		// JDA Command Initialisation
		// Phase 1
		registerCommand(DataCommand())
		registerCommand(HelpCommand())
		registerCommand(RedirectCommand())
		registerCommand(UptimeCommand())
	}

	@JvmStatic
	fun main(vararg arguments: String) {
		val token = getVariable("DISCORD_TOKEN")
		val connectionString = getVariable("MONGO_URI")
		val databaseName = getVariable("MONGO_DATABASE")
		botOwnerSnowflake = System.getenv("OWNER_SNOWFLAKE")?.toULongOrNull()?.toLong() ?: 0L

		jda = JDABuilder.createLight(token)
			.setEnabledIntents(
				listOf(
					GatewayIntent.MESSAGE_CONTENT,
					GatewayIntent.GUILD_MESSAGES
				)
			)
			.addEventListeners(EventListener {
				if (it is MessageReceivedEvent) {
					onMessageReceived(it)
				} else if (it is SlashCommandInteractionEvent) {
					onSlashCommandInteraction(it)
				}
			})
			.build()

		mongo = createClient(
			MongoClientSettings
				.builder()
				.applyConnectionString(ConnectionString(connectionString))
				.build()
		)

		database = mongo.getDatabase(databaseName)

		targetConfiguration = database.getCollection<TargetConfiguration>().apply {
			ensureIndex(TargetConfiguration::server)
		}

		// Phase 2
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
