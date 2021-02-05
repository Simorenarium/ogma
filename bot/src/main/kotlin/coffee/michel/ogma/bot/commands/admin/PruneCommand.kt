package coffee.michel.ogma.bot.commands.admin

import coffee.michel.ogma.bot.commands.Command
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component

@Component
internal class PruneCommand : Command() {

    override fun getName(): String = "Prune"

    override fun getDescription(): String = """
        `prune 10` - löscht die 10 neusten Nachrichten.
        `prune 100` - löscht die 100 neusten Nachrichten.
    """.trimIndent()

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean = startsWith(event, "prune")

    override fun handle(event: GuildMessageReceivedEvent) {
        if (!(event.member?.permissions ?: emptyList()).contains(Permission.MESSAGE_MANAGE)) {
            event.message.addReaction("❌").queue()
            return
        }

        val rawAmount = "\\d+".toRegex().find(event.message.contentStripped)?.value
        if (rawAmount == null) {
            event.message.reply("Es fehlt die Anzahl an Nachrichten.").queue()
            return
        }
        val amount = rawAmount.toInt()
        if (amount > 100) {
            event.message.reply("100 ist das maximum.").queue()
            return
        }

        event.channel.getHistoryBefore(event.messageId, amount)
            .queue { event.channel.deleteMessages(it.retrievedHistory).queue() }
        event.message.delete().queue()
    }
}