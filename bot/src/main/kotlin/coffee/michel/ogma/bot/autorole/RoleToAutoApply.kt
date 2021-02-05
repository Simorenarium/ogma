package coffee.michel.ogma.bot.autorole

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "RoleToAutoApply")
@Table(name = "rolestoautoapply")
internal data class RoleToAutoApply(
    @Id
    val id: Long,
    val guildId: Long,
    val roleId: Long,
    val event: String
)