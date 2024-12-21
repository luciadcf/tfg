package es.us.managemyteam.ui.view.verticalnavigation

import android.content.Context
import es.managemyteam.app.R

data class VerticalMenuVO(val id: VerticalMenuId, val title: String, val icon: Int) {

    companion object {
        fun getDefaultMenu(
            context: Context,
            isAdmin: Boolean,
            isStaff: Boolean
        ): List<VerticalMenuVO> {
            val verticalMenuList = mutableListOf<VerticalMenuVO>()

            val club = VerticalMenuVO(
                VerticalMenuId.MY_CLUB_ID,
                "Datos del club",
                R.drawable.ic_profile
            )
            val administration = VerticalMenuVO(
                VerticalMenuId.ADMINISTRATION_ID,
                "Administración del club",
                R.drawable.ic_settings
            )
            val team = VerticalMenuVO(
                VerticalMenuId.MY_TEAM_ID,
                context.resources.getString(R.string.my_team_title),
                R.drawable.ic_team
            )
            val call = VerticalMenuVO(
                VerticalMenuId.MY_CALLS_ID,
                context.resources.getString(R.string.calls),
                R.drawable.ic_call
            )
            val logout = VerticalMenuVO(
                VerticalMenuId.LOGOUT_ID,
                "Cerrar sesión",
                R.drawable.ic_settings
            )

            val myPayments = VerticalMenuVO(
                VerticalMenuId.PAYMENTS_ID,
                "Mis pagos",
                R.drawable.ic_euro
            )

            val termsAndConditions = VerticalMenuVO(
                VerticalMenuId.TERMS_CONDITIONS_ID,
                "Términos y condiciones",
                R.drawable.ic_terms
            )

            verticalMenuList.add(club)
            if (isAdmin) {
                verticalMenuList.add(administration)
            }

            if (isAdmin || isStaff) {
                verticalMenuList.add(team)
            }

            if (!isAdmin && !isStaff) {
                verticalMenuList.add(call)
            }

            if (!isAdmin) {
                verticalMenuList.add(myPayments)
            }

            verticalMenuList.add(termsAndConditions)

            verticalMenuList.add(logout)


            return verticalMenuList
        }
    }
}
