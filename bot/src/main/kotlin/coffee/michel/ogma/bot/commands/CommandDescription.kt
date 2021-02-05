package coffee.michel.ogma.bot.commands

import net.dv8tion.jda.api.EmbedBuilder

internal interface CommandDescription {

    fun getName(): String
    fun getDescription(): String

    fun applyDescriptionAsEmbed(embedBuilder: EmbedBuilder): EmbedBuilder = embedBuilder.appendDescription(getDescription())


}