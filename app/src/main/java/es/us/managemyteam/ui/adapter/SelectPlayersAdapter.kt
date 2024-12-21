package es.us.managemyteam.ui.adapter

import es.managemyteam.app.databinding.RowSelectPlayerBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import es.us.managemyteam.contract.UserSelectedListener
import es.us.managemyteam.data.model.UserCalledBo

class SelectPlayersAdapter :
    BaseAdapter<UserCalledBo, RowSelectPlayerBinding, SelectPlayersAdapter.SelectPlayerViewHolder>(),
    UserSelectedListener {

    override fun onCreate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): SelectPlayerViewHolder {
        return SelectPlayerViewHolder(RowSelectPlayerBinding.inflate(inflater, parent, false), this)
    }

    override fun onBind(item: UserCalledBo, position: Int, holder: SelectPlayerViewHolder) {
        holder.setup(holder.getViewBinding(), data[position])
    }

    fun getPlayers(selected: Boolean) = data.filter { it.called == selected }

    class SelectPlayerViewHolder(
        viewBinding: RowSelectPlayerBinding,
        private val playerSelectedListener: UserSelectedListener
    ) :
        BaseViewHolder<UserCalledBo, RowSelectPlayerBinding>(viewBinding) {
        override fun setup(viewBinding: RowSelectPlayerBinding, item: UserCalledBo) {
            viewBinding.rowSelectPlayerLabelName.text = item.userName
            viewBinding.rowSelectPlayerSwitcher.apply {
                setChecked(item.called)
                setOnCheckedChangeListener {
                    setChecked(it)
                    playerSelectedListener.onUserSelected(adapterPosition, it)
                }
            }
        }
    }

    override fun onUserSelected(position: Int, enable: Boolean) {
        data[position].called = enable
    }

}