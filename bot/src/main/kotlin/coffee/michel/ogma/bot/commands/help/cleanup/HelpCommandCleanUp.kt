package coffee.michel.ogma.bot.commands.help.cleanup

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
internal class HelpCommandCleanUp(
    private val jda: JDA,
    private val helpMessageRepository: HelpMessageRepository
) : ApplicationListener<ContextRefreshedEvent> {

    private val scheduledExecutor = Executors.newScheduledThreadPool(1)

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        scheduledExecutor.scheduleAtFixedRate({
            try {
                cleanHelpMessages()
            } catch (e: Exception) {
                log.error(e) { "An Error occurred when trying to clean left-over help messages." }
            }
        }, 0, 15, TimeUnit.MINUTES)
    }

    private fun cleanHelpMessages() {
        val oldestPossibleCreateDate = ZonedDateTime.now().minusMinutes(15)
        helpMessageRepository.findAll().filter { it.createDate.isBefore(oldestPossibleCreateDate) }
            .forEach { helpMessage ->
                jda.getGuildById(helpMessage.guildId)
                    ?.getTextChannelById(helpMessage.channelId)
                    ?.retrieveMessageById(helpMessage.messageId)
                    ?.queue { message ->
                        message.referencedMessage?.delete()?.queue()
                        message.delete().queue()
                    }
            }
    }


}