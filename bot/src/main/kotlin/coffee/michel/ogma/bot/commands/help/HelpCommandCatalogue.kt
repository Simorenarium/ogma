package coffee.michel.ogma.bot.commands.help

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

internal interface HelpCommandCatalogueEntry {

    companion object {
        val catalogue: List<HelpCommandCatalogueEntry> = listOf(
            HelpCommandDescription(),
            PingCommandDescription(),
            ExampleCommandDescription()
        )

        fun getPageUrl(desc: Any) = "https://ogma.rocks/bot/help/page/${catalogue.indexOf(desc) + 1}/${catalogue.size}"
        fun getPageFromUrl(url: String): Int = url.replace("https://ogma.rocks/bot/help/page/", "")
            .let { it.substring(0, it.indexOf('/')) }
            .let { Integer.valueOf(it) - 1 }
    }

    fun editWithHelpfulDescription(message: Message)

}

internal class HelpCommandDescription : HelpCommandCatalogueEntry {
    override fun editWithHelpfulDescription(message: Message) {
        message.editMessage(
            EmbedBuilder()
                .setTitle(
                    "Wie funktioniert die Hilfe?",
                    HelpCommandCatalogueEntry.getPageUrl(this)
                )
                .setDescription("Jeder Command hat einen Katalog-Eintrag.")
                .addField(MessageEmbed.Field("⬅️️", "Zeigt den vorherigen Eintrag", true))
                .addField(MessageEmbed.Field("➡️", "Zeigt den nächsten Eintrag", true))
                .build()
        ).queue()
    }

}

internal class PingCommandDescription : HelpCommandCatalogueEntry {
    override fun editWithHelpfulDescription(message: Message) {
        message.editMessage(
            EmbedBuilder()
                .setTitle(
                    "Ping",
                    HelpCommandCatalogueEntry.getPageUrl(this)
                )
                .setDescription("Antwortet mit \"Pong!\"")
                .build()
        ).queue()
    }

}

internal class ExampleCommandDescription : HelpCommandCatalogueEntry {
    override fun editWithHelpfulDescription(message: Message) {
        message.editMessage(
            EmbedBuilder()
                .setTitle(
                    "Example?",
                    HelpCommandCatalogueEntry.getPageUrl(this)
                )
                .setDescription("Is nur ein Beispiel zum testen.")
                .build()
        ).queue()
    }

}