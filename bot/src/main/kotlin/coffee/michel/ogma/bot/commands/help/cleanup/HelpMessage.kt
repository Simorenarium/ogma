package coffee.michel.ogma.bot.commands.help.cleanup

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "HelpMessage")
@Table(name = "helpmessage")
internal data class HelpMessage(
    @field:Id
    val messageId: Long,
    val guildId: Long,
    val channelId: Long,
    val createDate: ZonedDateTime
)