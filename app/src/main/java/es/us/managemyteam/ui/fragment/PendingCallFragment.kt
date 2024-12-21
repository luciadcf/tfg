package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentPendingCallsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.contract.AcceptListener
import es.us.managemyteam.contract.BaseAdapterClickListener
import es.us.managemyteam.contract.RejectReasonSendedListener
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.MyCallAdapter
import es.us.managemyteam.ui.viewmodel.PendingCallViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PendingCallFragment : BaseFragment<FragmentPendingCallsBinding>(), AcceptListener,
    BaseAdapterClickListener<EventBo>, RejectReasonSendedListener {

    companion object {

        fun newInstance() = PendingCallFragment()
    }

    private val callAdapter = MyCallAdapter(this, CallStatus.PENDING, this@PendingCallFragment)
    private val pendingCallViewModel: PendingCallViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupCallsObserver()
        setupAcceptPlayerObserver()
        setupRejectPlayerObserver()
    }


    private fun setupList() {
        viewBinding.pendingCallsListCalls.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewBinding.pendingCallsListCalls.adapter = callAdapter
    }

    private fun setupRejectPlayerObserver() {
        activity?.let {
            pendingCallViewModel.getRejectCallData()
                .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                    override fun onSuccess(response: Boolean?) {
                        response?.let {
                            showInformationDialog(getString(R.string.my_calls_success))
                        }
                    }

                    override fun onLoading(loading: Boolean) {
                        showLoader(loading)
                    }

                    override fun onError(error: Error) {
                        super.onError(error)
                        showErrorDialog(getString(error.errorMessageId))
                    }
                })
        }
    }

    private fun setupAcceptPlayerObserver() {
        activity?.let {
            pendingCallViewModel.getAcceptCallData()
                .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                    override fun onSuccess(response: Boolean?) {
                        response?.let {
                            showInformationDialog(getString(R.string.my_calls_success))
                        }
                    }

                    override fun onError(error: Error) {
                        super.onError(error)
                        showErrorDialog(getString(error.errorMessageId))
                    }
                })
        }
    }

    private fun setupCallsObserver() {
        val data = pendingCallViewModel.getPendingCallsData()
        viewLifecycleOwner.let {
            data.removeObservers(it)
            data.observe(it, object :
                ResourceObserver<List<EventBo>>() {
                override fun onSuccess(response: List<EventBo>?) {
                    response?.let { list ->
                        callAdapter.setData(list)
                        callAdapter.notifyDataSetChanged()
                        viewBinding.pendingCallsEmptyResults.root.visibility = if (list.isEmpty()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }

                override fun onLoading(loading: Boolean) {
                    showLoader(loading)
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(
                        error.serverErrorMessage ?: "",
                        getDefaultDialogErrorListener()
                    )
                }
            })
        }
        pendingCallViewModel.getPendingCalls()
    }

//region BaseFragment

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPendingCallsBinding {
        return FragmentPendingCallsBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.calls))
            setNavIcon(null)
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.show()
    }

//endregion

//region AcceptListener

    override fun onAccepted(uuid: String) {
        pendingCallViewModel.acceptCall(uuid)
    }

    override fun onRefused(uuid: String) {
        activity?.supportFragmentManager?.let { RejectReasonDialogFragment.show(it, uuid, this) }
    }

//endregion

    override fun onAdapterItemClicked(item: EventBo, position: Int) {
        findNavController().navigate(
            R.id.action_calls_to_event_detail, bundleOf(
                Pair(getString(R.string.navigation_event__uuid__argument), item.uuid)
            )
        )
    }

    override fun onRejectReasonSended(uuid: String, observation: String) {
        pendingCallViewModel.rejectCall(uuid, observation)

    }

}