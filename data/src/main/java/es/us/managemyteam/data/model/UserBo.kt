package es.us.managemyteam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserBo(
    var name: String? = null,
    var surname: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    val age: Int? = null,
    val role: Role? = null,
    val dorsal: Long? = null,
    var enable: Boolean? = null,
    var deviceInstanceId: String? = null
) : BaseBo(), Parcelable {

    fun getFullName() = (name ?: "").plus(" ").plus(surname ?: "")

    fun isAdmin() = Role.ADMIN == role

    fun isPlayer() = Role.PLAYER == role

    fun isStaff() = Role.STAFF == role

}

@Parcelize
data class RegistrationBo(
    var user: UserBo = UserBo(),
    var termsChecked: Boolean = false
) : Parcelable

enum class Role {
    ADMIN,
    STAFF,
    PLAYER
}