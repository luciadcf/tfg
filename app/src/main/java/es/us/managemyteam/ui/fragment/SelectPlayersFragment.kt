package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentSelectPlayersBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.CallBo
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.data.model.UserCalledBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.SelectPlayersAdapter
import es.us.managemyteam.ui.viewmodel.CreateEventViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SelectPlayersFragment : BaseFragment<FragmentSelectPlayersBinding>() {

    private var selectPlayersAdapter: SelectPlayersAdapter? = null
    private var currentEvent: EventBo? = null
    private val createEventViewModel: CreateEventViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupPlayersObserver()
        setupClickListener()
    }

    private fun setupClickListener() {
        viewBinding.selectPlayersBtnSave.setOnClickListener {
            generateCall()
        }
    }

    private fun generateCall() {
        val selectedPlayers = selectPlayersAdapter?.getPlayers(true) ?: arrayListOf()
        val unselectedPlayers = selectPlayersAdapter?.getPlayers(false) ?: arrayListOf()

        val call = CallBo(
            selectedPlayers,
            unselectedPlayers,
            Date(System.currentTimeMillis())
        )

        currentEvent?.let {
            it.call = call
            createEventViewModel.setCurrentNewEvent(it)
        }

        popBack()
    }

    private fun setupPlayersObserver() {
        createEventViewModel.getPlayersData()
            .observe(viewLifecycleOwner, object : ResourceObserver<List<UserBo>>() {
                override fun onSuccess(response: List<UserBo>?) {
                    response?.let { players ->
                        val usersCalled =
                            players.sortedBy { it.name }
                                .map { user ->
                                    UserCalledBo(
                                        user.uuid ?: "",
                                        user.getFullName(),
                                        false
                                    )
                                }
                        selectPlayersAdapter?.let {
                            it.setData(usersCalled)
                            it.notifyDataSetChanged()
                        }
                    }
                }
            })
        createEventViewModel.getCurrentNewEventData()
            .observe(viewLifecycleOwner, object : ResourceObserver<EventBo>() {
                override fun onSuccess(response: EventBo?) {
                    response?.let { event ->
                        currentEvent = event
                        val isEmpty = event.call?.called.isNullOrEmpty() &&
                                event.call?.notCalled.isNullOrEmpty()
                        if (isEmpty) {
                            createEventViewModel.getPlayers()
                        } else {
                            selectPlayersAdapter?.let {
                                it.setData(event.call?.called ?: arrayListOf())
                                it.addData(event.call?.notCalled ?: arrayListOf())
                                it.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })
        createEventViewModel.getCurrentNewEvent()
    }

    private fun setupList() {
        viewBinding.selectPlayersListPlayers.apply {
            if (selectPlayersAdapter == null) {
                selectPlayersAdapter = SelectPlayersAdapter()
            }
            selectPlayersAdapter?.let {
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
            setToolbarTitle(getString(R.string.create_event_select_call))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}