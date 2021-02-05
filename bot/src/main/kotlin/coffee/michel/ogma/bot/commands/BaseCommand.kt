package coffee.michel.ogma.bot.commands

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

internal interface BaseCommand {

    fun isTargeted(event: GuildMessageReceivedEvent): Boolean
    fun handle(event: GuildMessageReceivedEvent)

}