package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentRejectedCallsBinding
import android.content.DialogInterface
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
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.MyCallAdapter
import es.us.managemyteam.ui.viewmodel.RejectedCallViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RejectCallFragment : BaseFragment<FragmentRejectedCallsBinding>(), AcceptListener,
    BaseAdapterClickListener<EventBo> {

    companion object {
        fun newInstance() = RejectCallFragment()
    }

    private val callAdapter = MyCallAdapter(this, CallStatus.DENIED, this@RejectCallFragment)
    private val rejectedCallViewModel: RejectedCallViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupCallsObserver()
        setupAcceptPlayerObserver()
    }

    private fun setupList() {
        viewBinding.rejectedCallsListCalls.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewBinding.rejectedCallsListCalls.adapter = callAdapter
    }

    private fun setupAcceptPlayerObserver() {
        activity?.let {
            rejectedCallViewModel.getAcceptCallData()
                .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
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
        val data = rejectedCallViewModel.getRejectedCallsData()

        viewLifecycleOwner.let {
            data.removeObservers(it)
            data.observe(it, object :
                ResourceObserver<List<EventBo>>() {
                override fun onSuccess(response: List<EventBo>?) {
                    response?.let { list ->
                        callAdapter.setData(list)
                        callAdapter.notifyDataSetChanged()
                        viewBinding.rejectedCallsEmptyResults.root.visibility =
                            if (list.isEmpty()) {
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
                    showErrorDialog(error.serverErrorMessage ?: "",
                        DialogInterface.OnClickListener { _, _ ->
                            popBack()
                        })
                }
            })
        }
        rejectedCallViewModel.getRejectedCalls()
    }

//region BaseFragment

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRejectedCallsBinding {
        return FragmentRejectedCallsBinding.inflate(inflater, container, false)
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
        rejectedCallViewModel.acceptCall(uuid)
    }

    override fun onRefused(uuid: String) {
        // no-op
    }

//endregion

    override fun onAdapterItemClicked(item: EventBo, position: Int) {
        findNavController().navigate(
            R.id.action_calls_to_event_detail, bundleOf(
                Pair(getString(R.string.navigation_event__uuid__argument), item.uuid)
            )
        )
    }

}