package coffee.michel.ogma.bot.autorole

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import javax.annotation.PreDestroy

@Component
internal class AutoRoleApplicator(private val jda: JDA, private val rolesToAutoApply: RolesToAutoApply) :
    ApplicationListener<ContextRefreshedEvent>, ListenerAdapter() {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        jda.addEventListener(this)
    }

    @PreDestroy
    fun destroy() {
        jda.removeEventListener(this)
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val rolesToApply = rolesToAutoApply.findByGuildIdAndEvent(event.guild.idLong, "onJoin")
        if (rolesToApply.isEmpty())
            return

        rolesToApply.mapNotNull { event.guild.getRoleById(it.roleId) }
            .forEach { event.guild.addRoleToMember(event.member, it).queue() }
    }
}