package coffee.michel.ogma.bot.autorole.command

import coffee.michel.ogma.bot.autorole.RoleToAutoApply
import coffee.michel.ogma.bot.autorole.RolesToAutoApply
import coffee.michel.ogma.bot.commands.BaseCommand
import coffee.michel.ogma.bot.commands.CommandDescription
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component

private fun hasPermissions(event: GuildMessageReceivedEvent): Boolean {
    if (!(event.member?.permissions ?: emptyList()).contains(Permission.MANAGE_ROLES)) {
        event.message.addReaction("❌").queue()
        return false
    }
    return true
}

@Component
internal class RolesToAutoApplyCommandDescription : CommandDescription {

    override fun getName(): String = "Automatische Rollen"

    override fun getDescription(): String = """
        * `add role-to-apply onJoin <role>`
        * `remove role-to-apply onJoin <role>`
        
        Hinzugefügte Rollen werden automatisch hinzugefügt.
    """.trimIndent()

}

@Component
internal class AddRoleToApplyCommand(private var rolesToAutoApply: RolesToAutoApply) : BaseCommand {

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean =
        event.message.contentStripped.contains("add role-to-apply onJoin")

    override fun handle(event: GuildMessageReceivedEvent) {
        if (!hasPermissions(event)) return
        if (event.message.mentionedRoles.isEmpty()) {
            event.message.reply("Du musst eine Rolle erwähnen.")
            return
        }

        val role = event.message.mentionedRoles[0].idLong
        rolesToAutoApply.save(RoleToAutoApply(event.guild.idLong + role, event.guild.idLong, role, "onJoin"))

        event.message.addReaction("✅").queue()
    }
}

@Component
internal class RemoveRoleToApplyCommand(private var rolesToAutoApply: RolesToAutoApply) : BaseCommand {

    override fun isTargeted(event: GuildMessageReceivedEvent): Boolean =
        event.message.contentStripped.contains("remove role-to-apply onJoin")

    override fun handle(event: GuildMessageReceivedEvent) {
        if (!hasPermissions(event)) return
        if (event.message.mentionedRoles.isEmpty()) {
            event.message.reply("Du musst eine Rolle erwähnen.")
            return
        }

        val role = event.message.mentionedRoles[0].idLong
        rolesToAutoApply.deleteById(event.guild.idLong + role)

        event.message.addReaction("✅").queue()
    }
}