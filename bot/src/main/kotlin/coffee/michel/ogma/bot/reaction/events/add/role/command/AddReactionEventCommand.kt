package coffee.michel.ogma.bot.reaction.events.add.role.command

import coffee.michel.ogma.bot.commands.Command
import coffee.michel.ogma.bot.reaction.events.add.role.RoleToAddOnReaction
import coffee.michel.ogma.bot.reaction.events.add.role.RolesToAddOnReaction
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
internal class AddReactionEventCommand(
    private val rolesToAddOnReaction: RolesToAddOnReaction
) : Command() {
    //TODO if there is no remove, a cleanup may be necessary
    override fun getName(): String = "Add Reaction Event"

    override fun getDescription(): String = """
        Fügt ein Event hinzu, das von einer Reaction ausgelöst werden soll:
        * `add reaction event, add role @RoleName, on <emote>` sorgt dafür, 
        dass Rollen verteilt werden, wenn diese Reaction gedrückt wird.
        **Das funktioniert nur mit Emotes von dieser Gilde!** 
        
        Damit klar ist, welche bei welcher Nachricht auf Reaktionen gehört werden soll, muss der Command als Antwort verfasst sein.
    """.trimIndent()

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean = startsWith(event, "add reaction event")

    override fun handle(event: GuildMessageReceivedEvent) {
        val message = event.message

        if (message.contentStripped.contains("add role")) {
            createAddRoleTrigger(message)
        }
    }

    private fun createAddRoleTrigger(message: Message) {
        if (!isAddRoleTriggerValid(message)) return
        val refMessage = message.referencedMessage ?: return

        val role = message.mentionedRoles[0]
        val targetMessageId = refMessage.idLong

        rolesToAddOnReaction.save(
            RoleToAddOnReaction(
                targetMessageId + role.idLong,
                message.guild.idLong,
                targetMessageId,
                null,
                role.idLong
            )
        )
        message.delete().queue()

        refMessage.reply("Reagiere jetzt mit dem Emote.").queue()
    }

    private fun isAddRoleTriggerValid(message: Message): Boolean {
        if (message.mentionedRoles.isEmpty()) {
            message.reply("Du musst eine Rolle erwähnen: @RollenName").queue()
            return false
        }
        if (message.referencedMessage == null) {
            message.reply("Du musst auf eine Nachricht antworten, damit da was gemacht werden kann.").queue()
            return false
        }
        return true
    }
}