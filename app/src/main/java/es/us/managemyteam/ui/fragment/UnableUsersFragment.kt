package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentSelectPlayersBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.UnableUsersAdapter
import es.us.managemyteam.ui.viewmodel.UnableUsersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnableUsersFragment : BaseFragment<FragmentSelectPlayersBinding>() {

    private var unableUsersAdapter: UnableUsersAdapter? = null
    private val unableUsersViewModel: UnableUsersViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupUsersObserver()
        setupUsersUpdatedObserver()
        setupClickListener()
    }

    private fun setupClickListener() {
        viewBinding.selectPlayersBtnSave.setOnClickListener {
            unableUsersViewModel.updateUsers(unableUsersAdapter?.data ?: arrayListOf())
        }
    }

    private fun setupUsersObserver() {
        unableUsersViewModel.getUsersData()
            .observe(viewLifecycleOwner, object : ResourceObserver<List<UserBo>>() {
                override fun onSuccess(response: List<UserBo>?) {
                    response?.let {
                        unableUsersAdapter?.addData(it)
                        unableUsersAdapter?.notifyDataSetChanged()
                    }
                }

            })
        unableUsersViewModel.getUsers()
    }

    private fun setupUsersUpdatedObserver() {
        unableUsersViewModel.getUpdateUsersData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    response?.let {
                        if (it) {
                            Toast.makeText(
                                context,
                                getString(R.string.administration_unable_user_success),
                                Toast.LENGTH_LONG
                            ).show()
                            popBack()
                        }
                    }
                }

            })
    }

    private fun setupList() {
        viewBinding.selectPlayersListPlayers.apply {
            if (unableUsersAdapter == null) {
                unableUsersAdapter = UnableUsersAdapter()
            }
            unableUsersAdapter?.let {
                adapter = it
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectPlayersBinding {
        return FragmentSelectPlayersBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.administration_unable_user))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}