package es.us.managemyteam.ui.adapter

import es.managemyteam.app.databinding.RowAcceptPlayerBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import es.us.managemyteam.contract.AcceptListener
import es.us.managemyteam.data.model.UserBo

class AcceptPlayersAdapter(private val acceptListener: AcceptListener) :
    BaseAdapter<UserBo, RowAcceptPlayerBinding, AcceptPlayersAdapter.AcceptPlayersViewHolder>() {

    override fun onCreate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AcceptPlayersViewHolder {
        return AcceptPlayersViewHolder(
            RowAcceptPlayerBinding.inflate(inflater, parent, false),
            acceptListener
        )
    }

    override fun onBind(item: UserBo, position: Int, holder: AcceptPlayersViewHolder) {
        holder.setup(holder.getViewBinding(), data[position])
    }

    class AcceptPlayersViewHolder(
        viewBinding: RowAcceptPlayerBinding,
        private val acceptListener: AcceptListener
    ) :
        BaseAdapter.BaseViewHolder<UserBo, RowAcceptPlayerBinding>(viewBinding) {
        override fun setup(viewBinding: RowAcceptPlayerBinding, item: UserBo) {
            viewBinding.rowAcceptPlayerLabelName.text = item.name.plus(" ").plus(item.surname)
            viewBinding.rowAcceptPlayerBtnAccept.setOnClickListener {
                acceptListener.onAccepted(item.uuid ?: "")
            }
            viewBinding.rowAcceptPlayerBtnRefuse.setOnClickListener {
                acceptListener.onRefused(item.uuid ?: "")
            }
        }

    }

}