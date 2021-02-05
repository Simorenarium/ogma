package coffee.michel.ogma.bot.reaction.events.add.role

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import javax.annotation.PreDestroy

@Component
internal class AddRoleOnReactionEventHandler(
    private val jda: JDA,
    private val rolesToAddOnReaction: RolesToAddOnReaction
) : ApplicationListener<ContextRefreshedEvent>, ListenerAdapter() {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        jda.addEventListener(this)
    }

    @PreDestroy
    fun destroy() {
        jda.removeEventListener(this)
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        val emName = event.reactionEmote.name
        event.retrieveMessage().queue { message ->
            val existingReaction = message.reactions.find { it.reactionEmote.name == emName }
            if (existingReaction == null || !existingReaction.hasCount() || existingReaction.count == 1) {
                activateAutoAssign(event, emName)
            } else {
                assignRoleToUser(event, emName)
            }
        }
    }

    private fun activateAutoAssign(event: MessageReactionAddEvent, emoteName: String) {
        val inactiveRolesToAdd =
            rolesToAddOnReaction.findByGuildIdAndMessageIdAndEmoteIsNull(event.guild.idLong, event.messageIdLong)
        if (inactiveRolesToAdd.isEmpty()) return

        event.channel.getHistoryAfter(event.messageId, 10).queue {
            it.retrievedHistory.forEach { message ->
                if (message.referencedMessage?.idLong == event.messageIdLong && message.author.isBot)
                    message.delete().queue()
            }
        }

        rolesToAddOnReaction.save(inactiveRolesToAdd.first().copy(emote = emoteName))
    }

    private fun assignRoleToUser(event: MessageReactionAddEvent, emoteName: String) {
        val rolesToAddForMessage =
            rolesToAddOnReaction.findByGuildIdAndMessageId(event.guild.idLong, event.messageIdLong)

        val roleId = rolesToAddForMessage.find { it.emote == emoteName }?.targetRoleId ?: return
        val role = event.guild.getRoleById(roleId) ?: return

        event.guild.addRoleToMember(event.userIdLong, role).queue()
    }
}