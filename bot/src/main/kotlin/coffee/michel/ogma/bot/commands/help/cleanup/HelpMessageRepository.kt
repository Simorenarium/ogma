package coffee.michel.ogma.bot.commands.help.cleanup

import org.springframework.data.jpa.repository.JpaRepository

internal interface HelpMessageRepository : JpaRepository<HelpMessage, Long> {
}