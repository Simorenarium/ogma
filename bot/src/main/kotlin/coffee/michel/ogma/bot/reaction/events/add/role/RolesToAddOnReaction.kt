package coffee.michel.ogma.bot.commands.addReactionEvent

import org.springframework.data.jpa.repository.JpaRepository

internal interface RolesToAddOnReaction : JpaRepository<RoleToAddOnReaction, Long>