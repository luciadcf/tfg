package es.us.managemyteam.ui.fragment

import es.managemyteam.app.databinding.DialogRejectReasonBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import es.us.managemyteam.contract.RejectReasonSendedListener

const val DIALOG_REJECT_REASON = "dialog_reject_reason"
const val ARG_UUID = "arg_uuid"

class RejectReasonDialogFragment : DialogFragment() {

    private lateinit var viewBinding: DialogRejectReasonBinding
    var listener: RejectReasonSendedListener? = null
    private val callUuid: String by lazy { arguments?.getString(ARG_UUID) ?: "" }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            uuid: String,
            onRejectReasonSendedListener: RejectReasonSendedListener
        ) {
            RejectReasonDialogFragment().apply {
                listener = onRejectReasonSendedListener
                arguments = bundleOf(Pair(ARG_UUID, uuid))
            }.show(fragmentManager, DIALOG_REJECT_REASON)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DialogRejectReasonBinding.inflate(LayoutInflater.from(context))
        return viewBinding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
            it.setBackgroundDrawableResource(android.R.color.transparent)
            val params = it.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = params as WindowManager.LayoutParams
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.dialogRejectReasonImgClose.setOnClickListener {
            dialog?.dismiss()
        }
        viewBinding.dialogRejectReasonBtnSend.setOnClickListener {
            val reason = viewBinding.dialogRejectReasonInputRejectReason.text.toString().trim()
            if (reason.isNotBlank()) {
                viewBinding.dialogRejectReasonLabelError.visibility = GONE
                listener?.onRejectReasonSended(callUuid, reason.toString())
                dialog?.dismiss()
            } else {
                viewBinding.dialogRejectReasonLabelError.visibility = VISIBLE
            }
        }
    }

}