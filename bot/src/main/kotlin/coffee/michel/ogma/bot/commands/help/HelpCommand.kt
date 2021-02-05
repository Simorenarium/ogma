package coffee.michel.ogma.bot.commands.help

import coffee.michel.ogma.bot.commands.BaseCommand
import coffee.michel.ogma.bot.commands.Command
import coffee.michel.ogma.bot.commands.CommandDescription
import coffee.michel.ogma.bot.commands.help.cleanup.HelpMessage
import coffee.michel.ogma.bot.commands.help.cleanup.HelpMessageRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
internal class HelpCommand(
    private val cataloguePager: CataloguePageHandler,
    private val helpMessageRepository: HelpMessageRepository,
) : BaseCommand {

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean = event.message.contentStripped.contains("help")

    override fun handle(event: GuildMessageReceivedEvent) {
        val answerMessage = event.message.reply("Hier ist der Commando-Katalog:").complete()

        CompletableFuture.runAsync {
            // this is not really a critical operation
            helpMessageRepository.save(
                HelpMessage(
                    answerMessage.idLong,
                    answerMessage.channel.idLong,
                    answerMessage.guild.idLong,
                    answerMessage.timeCreated.toZonedDateTime()
                )
            )
        }

        cataloguePager.showCatalogue(answerMessage)
    }

}

@Component
internal class HelpCommandDescription : CommandDescription {
    override fun getName(): String = "Help"
    override fun getDescription(): String = """
        Der Help-Command öffnet diesen Command-Katalog.
    """.trimIndent()

    override fun applyDescriptionAsEmbed(embedBuilder: EmbedBuilder): EmbedBuilder =
        embedBuilder.appendDescription(getDescription())
            .addField(MessageEmbed.Field("⬅️️", "Zeigt den vorherigen Eintrag", true))
            .addField(MessageEmbed.Field("➡️", "Zeigt den nächsten Eintrag", true))
            .addField(MessageEmbed.Field("✅", "Löscht den Katalog. (Nach 15 Minuten automatisch)", false))
}