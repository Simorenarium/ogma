package coffee.michel.ogma.bot.reaction.events.add.role

import javax.persistence.*

@Entity(name = "RoleToAddOnReaction")
@Table(name = "rolestoaddonreaction")
internal data class RoleToAddOnReaction(
    @Id
    val id: Long,
    val guildId: Long,
    val messageId: Long,
    val emote: String?,
    val targetRoleId: Long
)