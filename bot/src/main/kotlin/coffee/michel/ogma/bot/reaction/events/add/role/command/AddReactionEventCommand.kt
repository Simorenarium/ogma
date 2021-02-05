package coffee.michel.ogma.bot.commands.addReactionEvent

import coffee.michel.ogma.bot.commands.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component

@Component
internal class AddReactionEventCommand(
    private val rolesToAddOnReaction: RolesToAddOnReaction
) : Command {

    override fun getName(): String = "Add Reaction Event"

    override fun getDescription(): String = """
        Fügt ein Event hinzu, das von einer Reaction ausgelöst werden soll:
        * `add reaction event, add role @RoleName, on <emote>` sorgt dafür, dass Rollen verteilt werden, wenn diese Reaction gedrückt wird.
        
        Damit klar ist, welche bei welcher Nachricht auf Reaktionen gehört werden soll, muss der Command als Antwort verfasst sein.
    """.trimIndent()

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean = event.message.contentStripped.contains("add reaction event")

    override fun handle(event: GuildMessageReceivedEvent) {
        val message = event.message

        if(message.contentStripped.contains("add role")) {
            createAddRoleTrigger(message)
        }

    }

    private fun createAddRoleTrigger(message: Message) {
        if(!isAddRoleTriggerValid(message)) return

        val role = message.mentionedRoles[0]
        val emote = message.emotes[0].name
        val targetMessageId = message.referencedMessage!!.idLong

        rolesToAddOnReaction.save(RoleToAddOnReaction(null,message.guild.idLong, targetMessageId, emote, role.idLong))
        message.referencedMessage!!.addReaction(message.emotes[0]).queue()
        message.delete().queue()
    }

    private fun isAddRoleTriggerValid(message: Message): Boolean {
        if(message.mentionedRoles.isEmpty()) {
            message.reply("Du musst eine Rolle erwähnen: @RollenName").queue()
            return false
        }
        if(message.emotes.isEmpty()) {
            message.reply("Du musst einen emote da lassen auf den geklickt werden kann.").queue()
            return false
        }
        if(message.referencedMessage == null) {
            message.reply("Du musst auf eine Nachricht antworten, damit da was gemacht werden kann.").queue()
            return false
        }
        return true
    }
}