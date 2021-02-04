package coffee.michel.ogma.bot

import coffee.michel.ogma.bot.commands.Command
import coffee.michel.ogma.bot.commands.help.HelpCommand
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

private val log = KotlinLogging.logger {}

@Component
internal class EventPublisher(
    private val jda: JDA,
    private val commands: List<Command>
) : ApplicationListener<ContextRefreshedEvent>, ListenerAdapter() {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        jda.addEventListener(this)
    }

    @PreDestroy
    fun tearDown() {
        jda.removeEventListener(this)
    }


    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {

        if (event.message.isMentioned(jda.selfUser))
            callCommand(event)
    }

    private fun callCommand(event: GuildMessageReceivedEvent) {
        if (event.message.contentStripped.matches(Regex(".*ping.*", RegexOption.IGNORE_CASE))) {
            log.info { "Received: ${event.message.contentStripped}" }
            event.message.reply("Pong!").complete()
        }

        commands.forEach {
            if (it.isTargeted(event)) {
                CompletableFuture.runAsync {
                    try {
                        it.handle(event)
                    } catch (e: Exception) {
                        log.error(e) { "Error while executing a command." }
                    }
                }
            }
        }
    }
}