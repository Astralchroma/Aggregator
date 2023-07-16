package dev.astralchroma.aggregator

import dev.astralchroma.aggregator.annotations.*
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

abstract class CommandClass {
	private val lookup = MethodHandles.lookup()

	fun constructCommandData(): Pair<SlashCommandData, Map<String, (SlashCommandInteractionEvent) -> Unit>> {
		// Base command name and description
		val commandMeta: Command = this::class.findAnnotation()
			?: throw Exception("Command class ${this::class.simpleName} has no command annotation")

		// Permissions
		val permissionMeta =
			this::class.findAnnotation<EnabledFor>()?.permissions?.let { DefaultMemberPermissions.enabledFor(*it) }
				?: DefaultMemberPermissions.ENABLED

		// Base command function (if any)
		val commandFunction = this::class.memberFunctions.find { it.hasAnnotation<Default>() }

		// Base command slash command data
		val slashCommandData = Commands.slash(commandMeta.name, commandMeta.description)
			.setGuildOnly(commandMeta.guildOnly)
			.setDefaultPermissions(permissionMeta)

		// If there is a base command that means there are no subcommands
		if (commandFunction != null) {
			val (command, executor) = constructExecutor(this::class, commandFunction)

			return Pair(slashCommandData.addOptions(command), mapOf(commandMeta.name to executor))
		}

		// Handle all possible subcommands
		val subcommandFunctions = this::class.memberFunctions.filter { it.hasAnnotation<Subcommand>() }

		// If there are no subcommands or base command
		if (subcommandFunctions.isEmpty()) {
			throw Exception("Command class ${this::class.simpleName} has no @Command or @Subcommand")
		}

		// command + subcommand -> command executor
		val subcommandExecutionData = mutableMapOf<String, (SlashCommandInteractionEvent) -> Unit>()

		val subcommandData = subcommandFunctions.map { subcommandFunction ->
			// Will not be null as a function would have to have this annotation in order for it to be referenced here.
			val subcommandMeta = subcommandFunction.findAnnotation<Subcommand>()!!

			val (command, executor) = constructExecutor(this::class, subcommandFunction)

			subcommandExecutionData["${commandMeta.name} ${subcommandMeta.name}"] = executor // Executor
			SubcommandData(subcommandMeta.name, subcommandMeta.description).addOptions(command) // Subcommand Data
		}

		// Final command data + executor
		return Pair(slashCommandData.addSubcommands(subcommandData), subcommandExecutionData)
	}

	private fun constructExecutor(clazz: KClass<*>, function: KFunction<*>): Pair<List<OptionData>, (SlashCommandInteractionEvent) -> Unit> {
		val optionData: List<Pair<OptionData?, (SlashCommandInteractionEvent) -> Any>> = function.parameters.map { parameter ->
			val jvmErasure = parameter.type.jvmErasure

			if (jvmErasure == SlashCommandInteractionEvent::class) return@map Pair(null) { it } // Event
			if (parameter.kind == KParameter.Kind.INSTANCE) return@map Pair(null) { this } // Instance Parameter

			val parameterMeta: Parameter = parameter.findAnnotation()
				?: throw Exception("Missing @Parameter annotation for ${parameter.name} in ${clazz.jvmName}#${function.name}")

			val parameterName = parameterMeta.name

			val (type: OptionType?, accessor: (SlashCommandInteractionEvent) -> Any) =
				when (jvmErasure) {
					TextChannel::class -> Pair(OptionType.CHANNEL) { event: SlashCommandInteractionEvent ->
						event.getOption(parameterName)?.asChannel as? TextChannel
							?: throw NullPointerException("Unable to get TextChannel for $parameterName.")
					}
					Attachment::class -> Pair(OptionType.ATTACHMENT) { event: SlashCommandInteractionEvent ->
						event.getOption(parameterName)?.asAttachment
							?: throw NullPointerException("Unable to get Attachment for $parameterName")
					}

					else -> throw Exception("Unsupported type ${jvmErasure.jvmName}")
				}

			var optionData = OptionData(type, parameterName, parameterMeta.description, parameter.isOptional)
			if (type == OptionType.CHANNEL) optionData = optionData.setChannelTypes(*parameterMeta.channelTypes)

			Pair(optionData, accessor)
		}

		val handle = lookup.unreflect(function.javaMethod)

		return Pair(optionData.mapNotNull { it.first }) { event: SlashCommandInteractionEvent ->
			handle.invokeWithArguments(*optionData.map { it.second(event) }.toTypedArray())
		}
	}
}
