package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentAcceptsPlayersBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.contract.AcceptListener
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.AcceptPlayersAdapter
import es.us.managemyteam.ui.viewmodel.AcceptPlayersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AcceptPlayersFragment : BaseFragment<FragmentAcceptsPlayersBinding>(), AcceptListener {

    private var acceptPlayersAdapter: AcceptPlayersAdapter? = null
    private val acceptsPlayersViewModel: AcceptPlayersViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupPlayersObserver()
        setupAcceptPlayerObserver()
    }

    private fun setupAcceptPlayerObserver() {
        acceptsPlayersViewModel.getAcceptPlayerData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    response?.let {
                        acceptsPlayersViewModel.getPlayers()
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }
            })
    }

    private fun setupPlayersObserver() {
        acceptsPlayersViewModel.getPlayersData()
            .observe(viewLifecycleOwner, object : ResourceObserver<List<UserBo>>() {
                override fun onSuccess(response: List<UserBo>?) {
                    response?.let {
                        if (response.isNotEmpty()) {
                            viewBinding.acceptsPlayerViewEmptyRequests.root.visibility = GONE
                        } else {
                            viewBinding.acceptsPlayerViewEmptyRequests.root.visibility = VISIBLE
                        }
                        acceptPlayersAdapter?.apply {
                            setData(it)
                            notifyDataSetChanged()
                        }
                    }
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    showLoader(loading)
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }
            })
        acceptsPlayersViewModel.getPlayers()
    }

    private fun setupList() {
        viewBinding.acceptsPlayerListUsers.apply {
            if (acceptPlayersAdapter == null) {
                acceptPlayersAdapter = AcceptPlayersAdapter(this@AcceptPlayersFragment)
            }
            acceptPlayersAdapter?.let {
                adapter = it
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAcceptsPlayersBinding {
        return FragmentAcceptsPlayersBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.administration_accept_players))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.show()
    }

    override fun onAccepted(uuid: String) {
        acceptsPlayersViewModel.acceptPlayer(uuid)
    }

    override fun onRefused(uuid: String) {
        acceptsPlayersViewModel.rejectPlayer(uuid)
    }
}