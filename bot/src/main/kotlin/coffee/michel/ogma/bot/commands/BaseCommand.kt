package coffee.michel.ogma.bot.commands

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

internal abstract class BaseCommand {

    abstract fun isTargeted(event: GuildMessageReceivedEvent): Boolean
    abstract fun handle(event: GuildMessageReceivedEvent)

    protected fun startsWith(event: GuildMessageReceivedEvent, searchTerm: String): Boolean =
        event.message.contentRaw.replace(Regex("<.*>"), "").trim().startsWith(searchTerm)


}