package coffee.michel.ogma.bot.commands.help

import coffee.michel.ogma.bot.commands.Command
import coffee.michel.ogma.bot.commands.help.cleanup.HelpCommandCleanUp
import coffee.michel.ogma.bot.commands.help.cleanup.HelpMessage
import coffee.michel.ogma.bot.commands.help.cleanup.HelpMessageRepository
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
internal class HelpCommand(
    private val cataloguePager: CataloguePageHandler,
    private val helpMessageRepository: HelpMessageRepository
) : Command {

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean = event.message.contentStripped.contains("help")

    override fun handle(event: GuildMessageReceivedEvent) {
        val answerMessage = event.message.reply("moment...").complete()

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