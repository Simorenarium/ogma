package coffee.michel.ogma.bot.commands.help

import coffee.michel.ogma.bot.commands.help.cleanup.HelpMessageRepository
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
internal class CataloguePageHandler(
    private val jda: JDA,
    private val helpMessageRepository: HelpMessageRepository
) : ListenerAdapter() {

    companion object {
        private const val PREV_PAGE = "⬅"
        private const val NEXT_PAGE = "➡"
        private const val DONE = "✅"
    }

    private val allowedMessages: MutableList<Long> = LinkedList()

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    @PreDestroy
    fun tearDown() {
        jda.removeEventListener(this)
    }

    fun showCatalogue(message: Message) {
        allowedMessages.add(message.idLong)

        message.addReaction(PREV_PAGE).queue()
        message.addReaction(NEXT_PAGE).queue()
        message.addReaction(DONE).queue()

        HelpCommandCatalogueEntry.catalogue.first().editWithHelpfulDescription(message)
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (!allowedMessages.contains(event.messageIdLong))
            return
        if(event.userIdLong == jda.selfUser.idLong)
            return
        val reactionType = event.reactionEmote.emoji

        event.reaction.removeReaction(event.user!!).queue()
        event.retrieveMessage().queue { message ->
            val url = message.embeds.first().url ?: return@queue
            val currentPage = HelpCommandCatalogueEntry.getPageFromUrl(url)

            if (reactionType == NEXT_PAGE && currentPage < HelpCommandCatalogueEntry.catalogue.size)
                HelpCommandCatalogueEntry.catalogue[currentPage + 1].editWithHelpfulDescription(message)
            else if (reactionType == PREV_PAGE && currentPage > 0)
                HelpCommandCatalogueEntry.catalogue[currentPage - 1].editWithHelpfulDescription(message)
            else if (reactionType == DONE) {
                val messageId = message.idLong

                message.referencedMessage?.delete()?.queue()
                message.delete().queue()

                CompletableFuture.runAsync { helpMessageRepository.deleteById(messageId) }
            }
        }

    }

}