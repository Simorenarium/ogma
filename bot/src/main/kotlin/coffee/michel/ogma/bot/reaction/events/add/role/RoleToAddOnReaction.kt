package coffee.michel.ogma.bot.commands.addReactionEvent

import javax.persistence.*

@Entity(name = "RoleToAddOnReaction")
@Table(name = "rolestoaddonreaction")
internal data class RoleToAddOnReaction(
    @Id
    val id: Long,
    val guildId: Long,
    val messageId: Long,
    val emoteName: String,
    val targetRoleId: Long
)