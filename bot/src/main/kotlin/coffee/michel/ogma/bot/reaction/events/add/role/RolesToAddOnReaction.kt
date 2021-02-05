package coffee.michel.ogma.bot.reaction.events.add.role

import org.springframework.data.jpa.repository.JpaRepository

internal interface RolesToAddOnReaction : JpaRepository<RoleToAddOnReaction, Long> {

    fun findByGuildIdAndMessageId(guildId:Long, messageId: Long): List<RoleToAddOnReaction>
    fun findByGuildIdAndMessageIdAndEmoteIsNull(guildId: Long, messageIdLong: Long): List<RoleToAddOnReaction>

}