package coffee.michel.ogma.bot.commands.help

import coffee.michel.ogma.bot.commands.CommandDescription
import coffee.michel.ogma.bot.commands.help.cleanup.HelpMessageRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
internal class CataloguePageHandler(
    private val jda: JDA,
    private val helpMessageRepository: HelpMessageRepository,
    private val commands: List<CommandDescription>
) : ListenerAdapter() {

    companion object {
        private const val PREV_PAGE = "⬅"
        private const val NEXT_PAGE = "➡"
        private const val DONE = "✅"
    }

    private val allowedMessages: MutableList<Long> = LinkedList()

    private lateinit var sortedCommands: List<CommandDescription>

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
        sortedCommands = commands.stream().sorted { a, b ->
            if (a.getName() == "Help") -1
            else a.getName().compareTo(b.getName())
        }.collect(Collectors.toList())
    }

    @PreDestroy
    fun tearDown() {
        jda.removeEventListener(this)
    }

    fun showCatalogue(message: Message) {
        allowedMessages.add(message.idLong)


        //TODO implement search

        // should always be help
        val cmd = sortedCommands.first()
        val embedBuilder = initEmbedFor(cmd)
        cmd.applyDescriptionAsEmbed(embedBuilder)
        message.editMessage(embedBuilder.build()).queue()

        message.addReaction(PREV_PAGE).queue()
        message.addReaction(NEXT_PAGE).queue()
        message.addReaction(DONE).queue()
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (!allowedMessages.contains(event.messageIdLong))
            return
        if (event.userIdLong == jda.selfUser.idLong)
            return
        val reactionType = event.reactionEmote.emoji

        event.reaction.removeReaction(event.user!!).queue()
        event.retrieveMessage().queue { message ->
            val footer = message.embeds.first().footer?.text ?: return@queue
            val currentPage = getPageFromHelpFooter(footer)

            var command: CommandDescription? = null

            if (reactionType == NEXT_PAGE && currentPage < sortedCommands.size - 1)
                command = sortedCommands[currentPage + 1]
            else if (reactionType == PREV_PAGE && currentPage > 0)
                command = sortedCommands[currentPage - 1]
            else if (reactionType == DONE) {
                val messageId = message.idLong

                message.referencedMessage?.delete()?.queue()
                message.delete().queue()

                CompletableFuture.runAsync { helpMessageRepository.deleteById(messageId) }
            }

            // this looks weird, but the paging should not be in the command

            val embed = initEmbedFor(command ?: return@queue)
            message.editMessage(command.applyDescriptionAsEmbed(embed).build()).queue()
        }

    }

    fun initEmbedFor(command: CommandDescription) = EmbedBuilder()
        .setTitle(command.getName())
        .setFooter(getHelpPageInFooter(command))

    fun getHelpPageInFooter(desc: CommandDescription) = "${sortedCommands.indexOf(desc) + 1}/${sortedCommands.size}"

    fun getPageFromHelpFooter(footer: String): Int = Integer.valueOf(footer.split("/")[0]) - 1

}