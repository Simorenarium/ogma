package coffee.michel.ogma.bot.autorole

import org.springframework.data.jpa.repository.JpaRepository

internal interface RolesToAutoApply : JpaRepository<RoleToAutoApply, Long> {

    fun findByGuildIdAndEvent(guildId: Long, event: String) : List<RoleToAutoApply>
    fun deleteByRoleId(roleId: Long)
}