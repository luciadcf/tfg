package es.us.managemyteam.ui.adapter

import es.managemyteam.app.databinding.RowAcceptPlayerBinding
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import es.us.managemyteam.contract.AcceptListener
import es.us.managemyteam.contract.BaseAdapterClickListener
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.EventBo
import java.util.Date

class MyCallAdapter(
    private val acceptListener: AcceptListener,
    private val status: CallStatus,
    private val itemListener: BaseAdapterClickListener<EventBo>
) :
    BaseAdapter<EventBo, RowAcceptPlayerBinding, MyCallAdapter.AcceptCallViewHolder>() {

    override fun onCreate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AcceptCallViewHolder {
        return AcceptCallViewHolder(
            RowAcceptPlayerBinding.inflate(inflater, parent, false),
            acceptListener,
            status,
            itemListener
        )
    }

    override fun onBind(item: EventBo, position: Int, holder: AcceptCallViewHolder) {
        holder.setup(holder.getViewBinding(), item)
    }

    class AcceptCallViewHolder(
        viewBinding: RowAcceptPlayerBinding,
        private val acceptListener: AcceptListener,
        private val status: CallStatus,
        private val itemListener: BaseAdapterClickListener<EventBo>
    ) :
        BaseAdapter.BaseViewHolder<EventBo, RowAcceptPlayerBinding>(viewBinding) {
        override fun setup(viewBinding: RowAcceptPlayerBinding, item: EventBo) {
            viewBinding.rowAcceptPlayerLabelName.text = item.title
            viewBinding.rowAcceptPlayerBtnAccept.setOnClickListener {
                acceptListener.onAccepted(item.uuid ?: "")
            }
            viewBinding.rowAcceptPlayerBtnRefuse.setOnClickListener {
                acceptListener.onRefused(item.uuid ?: "")
            }
            item.date?.let {
                if (it.before(Date())) {
                    viewBinding.rowAcceptPlayerBtnAccept.visibility = GONE
                    viewBinding.rowAcceptPlayerBtnRefuse.visibility = GONE
                } else {
                    setButtonsVisibility(viewBinding)
                }
            } ?: run {
                setButtonsVisibility(viewBinding)
            }

            viewBinding.root.setOnClickListener {
                itemListener.onAdapterItemClicked(item, adapterPosition)
            }
        }

        private fun setButtonsVisibility(viewBinding: RowAcceptPlayerBinding) {
            when (status) {
                CallStatus.PENDING -> {
                    viewBinding.rowAcceptPlayerBtnAccept.visibility = VISIBLE
                    viewBinding.rowAcceptPlayerBtnRefuse.visibility = VISIBLE
                }

                CallStatus.ACCEPTED -> {
                    viewBinding.rowAcceptPlayerBtnAccept.visibility = GONE
                    viewBinding.rowAcceptPlayerBtnRefuse.visibility = VISIBLE
                }

                else -> {
                    viewBinding.rowAcceptPlayerBtnAccept.visibility = VISIBLE
                    viewBinding.rowAcceptPlayerBtnRefuse.visibility = GONE
                }
            }
        }
    }

}