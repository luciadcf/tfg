package es.us.managemyteam.ui.adapter

import es.managemyteam.app.databinding.RowSelectPlayerBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import es.us.managemyteam.contract.UserSelectedListener
import es.us.managemyteam.data.model.Role
import es.us.managemyteam.data.model.UserBo

class UnableUsersAdapter :
    BaseAdapter<UserBo, RowSelectPlayerBinding, UnableUsersAdapter.UnableUserViewHolder>(),
    UserSelectedListener {

    override fun onCreate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): UnableUserViewHolder {
        return UnableUserViewHolder(RowSelectPlayerBinding.inflate(inflater, parent, false), this)
    }

    override fun onBind(item: UserBo, position: Int, holder: UnableUserViewHolder) {
        holder.setup(holder.getViewBinding(), data[position])
    }

    class UnableUserViewHolder(
        viewBinding: RowSelectPlayerBinding,
        private val playerSelectedListener: UserSelectedListener
    ) :
        BaseViewHolder<UserBo, RowSelectPlayerBinding>(viewBinding) {
        override fun setup(viewBinding: RowSelectPlayerBinding, item: UserBo) {
            viewBinding.rowSelectPlayerLabelName.text =
                item.getFullName().plus(getStringRole(item.role))
            viewBinding.rowSelectPlayerSwitcher.apply {
                setChecked(item.enable ?: false)
                setOnCheckedChangeListener {
                    setChecked(it)
                    playerSelectedListener.onUserSelected(adapterPosition, it)
                }
            }
        }

        private fun getStringRole(role: Role?): String {
            role?.let {
                val stringRole = when (role) {
                    Role.STAFF -> "Entrenador"
                    Role.PLAYER -> "Jugador"
                    Role.ADMIN -> "Admin"
                }
                return " (".plus(stringRole).plus(")")
            } ?: run {
                return ""
            }

        }
    }

    override fun onUserSelected(position: Int, enable: Boolean) {
        data[position].enable = enable
    }


}