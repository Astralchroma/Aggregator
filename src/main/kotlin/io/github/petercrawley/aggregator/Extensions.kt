package io.github.petercrawley.aggregator

import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.AuthorInfo
import net.dv8tion.jda.api.entities.MessageEmbed.Field
import net.dv8tion.jda.api.entities.MessageEmbed.Footer
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo
import net.dv8tion.jda.api.entities.MessageEmbed.Provider
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail
import net.dv8tion.jda.api.entities.MessageEmbed.VideoInfo
import java.time.OffsetDateTime

// Helper function for constructing a MessageEmbed in a more Kotlin idiomatic way.
@Suppress("NOTHING_TO_INLINE")
inline fun messageEmbed(
	url: String? = null, title: String? = null, description: String? = null, type: EmbedType = EmbedType.RICH,
	timestamp: OffsetDateTime? = null, color: Int = 0, thumbnail: Thumbnail? = null, siteProvider: Provider? = null,
	author: AuthorInfo? = null, videoInfo: VideoInfo? = null, footer: Footer? = null, image: ImageInfo? = null,
	fields: List<Field> = listOf()
) = MessageEmbed(
	url, title, description, type, timestamp, color, thumbnail, siteProvider, author, videoInfo, footer, image, fields
)
