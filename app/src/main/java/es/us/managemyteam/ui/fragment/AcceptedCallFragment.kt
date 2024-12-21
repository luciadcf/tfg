package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentAcceptedCallsBinding
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import es.us.managemyteam.ui.viewmodel.AcceptedCallViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AcceptedCallFragment : BaseFragment<FragmentAcceptedCallsBinding>(), AcceptListener,
    BaseAdapterClickListener<EventBo>, RejectReasonSendedListener {

    companion object {
        fun newInstance() = AcceptedCallFragment()
    }

    private val callAdapter = MyCallAdapter(this, CallStatus.ACCEPTED, this@AcceptedCallFragment)
    private val acceptedCallViewModel: AcceptedCallViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupCallsObserver()
        setupRejectPlayerObserver()
    }

    private fun setupList() {
        viewBinding.acceptedCallsListCalls.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewBinding.acceptedCallsListCalls.adapter = callAdapter
    }

    private fun setupRejectPlayerObserver() {
        activity?.let {
            acceptedCallViewModel.getRejectCallData()
                .observe(it, object : ResourceObserver<Boolean>() {
                    override fun onSuccess(response: Boolean?) {
                        response?.let {
                            showInformationDialog("Tu respuesta se envió con éxito")
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

    private fun setupCallsObserver() {
        val data = acceptedCallViewModel.getAcceptedCallsData()

        data.removeObservers(viewLifecycleOwner)
        data.observe(viewLifecycleOwner, object :
            ResourceObserver<List<EventBo>>() {
            override fun onSuccess(response: List<EventBo>?) {
                response?.let { list ->
                    callAdapter.setData(list)
                    callAdapter.notifyDataSetChanged()
                    viewBinding.acceptedCallsEmptyResults.root.visibility = if (list.isEmpty()) {
                        VISIBLE
                    } else {
                        GONE
                    }
                }
            }

            override fun onLoading(loading: Boolean) {
                showLoader(loading)
            }

            override fun onError(error: Error) {
                super.onError(error)
                showErrorDialog(error.serverErrorMessage ?: "",
                    DialogInterface.OnClickListener { _, _ ->
                        popBack()
                    })
            }
        })
        acceptedCallViewModel.getAcceptedCalls()
    }

//region BaseFragment

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAcceptedCallsBinding {
        return FragmentAcceptedCallsBinding.inflate(inflater, container, false)
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
        // no-op
    }

    override fun onRefused(uuid: String) {
        activity?.supportFragmentManager?.let { RejectReasonDialogFragment.show(it, uuid, this) }
    }

    override fun onAdapterItemClicked(item: EventBo, position: Int) {
        findNavController().navigate(
            R.id.action_calls_to_event_detail, bundleOf(
                Pair(getString(R.string.navigation_event__uuid__argument), item.uuid)
            )
        )
    }

    override fun onRejectReasonSended(uuid: String, observation: String) {
        acceptedCallViewModel.rejectCall(uuid, observation)
    }

//endregion

}