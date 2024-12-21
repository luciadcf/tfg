package es.us.managemyteam.ui.adapter

import es.managemyteam.app.R
import es.managemyteam.app.databinding.RowPlayerBinding
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.UserCalledBo

class PlayerAdapter(private val userIsPlayer: Boolean) :
    BaseAdapter<UserCalledBo, RowPlayerBinding, PlayerAdapter.PlayerViewHolder>() {

    override fun onCreate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): PlayerViewHolder {
        return PlayerViewHolder(userIsPlayer, RowPlayerBinding.inflate(inflater, parent, false))
    }

    override fun onBind(item: UserCalledBo, position: Int, holder: PlayerViewHolder) {
        holder.setup(item = item)
    }

    class PlayerViewHolder(
        private val userIsPlayer: Boolean,
        binding: RowPlayerBinding
    ) :
        BaseAdapter.BaseViewHolder<UserCalledBo, RowPlayerBinding>(binding) {
        override fun setup(viewBinding: RowPlayerBinding, item: UserCalledBo) {
            val number = adapterPosition + 1
            viewBinding.rowPlayerLabelName.text = ("$number.").plus(" " + item.userName)
            if (!userIsPlayer) {
                setupCallStatus(item.enable)
                if (item.enable == CallStatus.DENIED.ordinal) {
                    viewBinding.root.setOnClickListener {
                        viewBinding.rowPlayerLabelRejectReason.text = item.observation
                        val actualVisibility = viewBinding.rowPlayerContainerRejectReason.visibility
                        viewBinding.rowPlayerContainerRejectReason.visibility =
                            if (actualVisibility == VISIBLE) {
                                GONE
                            } else {
                                VISIBLE
                            }
                    }
                }
            } else {
                viewBinding.rowPlayerImgCallStatus.visibility = GONE
                viewBinding.rowPlayerContainerRejectReason.visibility = GONE
            }
        }

        private fun setupCallStatus(enable: Int) {
            val context = getViewBinding().root.context
            val img = when (enable) {
                CallStatus.ACCEPTED.ordinal -> ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_accept
                )

                CallStatus.DENIED.ordinal -> ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_refuse
                )

                else -> ContextCompat.getDrawable(context, R.drawable.ic_pending)
            }

            getViewBinding().rowPlayerImgCallStatus.setImageDrawable(img)
        }

    }

}