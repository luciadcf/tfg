package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentEventDetailBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.PlayerAdapter
import es.us.managemyteam.ui.viewmodel.EventDetailViewModel
import es.us.managemyteam.util.DateUtil
import es.us.managemyteam.util.PermissionUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

private val PERMISSIONS = arrayOf(android.Manifest.permission.WRITE_CALENDAR)

class EventDetailFragment : BaseFragment<FragmentEventDetailBinding>() {

    private val eventDetailViewModel: EventDetailViewModel by viewModel()
    private var userIsPlayer = false
    private var playersAdapter: PlayerAdapter? = null
    private var eventId = ""
    private var currentEvent: EventBo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupUserObserver()
        setupEventDetailObserver()
        arguments?.getString(getString(R.string.navigation_event__uuid__argument))?.let {
            eventId = it
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.isPermissionGranted(requestCode, grantResults)) {
            saveEvent()
        }
    }

    private fun setupList() {
        viewBinding.eventDetailListCalled.apply {
            if (playersAdapter == null) {
                playersAdapter = PlayerAdapter(userIsPlayer)
            }
            playersAdapter?.let {
                adapter = it
            }
        }
        viewBinding.eventDetailListCalled.adapter = playersAdapter
    }


    private fun setupClickListeners() {
        viewBinding.eventDetailImgBack.setOnClickListener {
            popBack()
        }
        viewBinding.eventDetailImgAddToCalendar.setOnClickListener {
            clickOnAddToCalendar()
        }
        viewBinding.eventDetailBtnGoToLocation.setOnClickListener {
            currentEvent?.location?.location?.toLatLng()?.let { latlng ->
                context?.startGoogleMapsByLatLng(
                    latlng
                )
            }
        }
    }

    private fun clickOnAddToCalendar() {
        context?.let { context ->
            if (PermissionUtil.needPermission(context, *PERMISSIONS)) {
                PermissionUtil.requestPermission(this, *PERMISSIONS)
            } else {
                saveEvent()
            }
        }
    }

    private fun saveEvent() {
        context?.startCalendarEvent(
            currentEvent?.title,
            currentEvent?.date?.time,
            currentEvent?.location
        )
    }

    private fun setupEventDetailObserver() {
        eventDetailViewModel.getEventDetailData()
            .observe(viewLifecycleOwner, object : ResourceObserver<EventBo>() {
                override fun onSuccess(response: EventBo?) {
                    response?.let {
                        currentEvent = it
                        setupView(it)
                    }
                }

            })
    }

    private fun setupUserObserver() {
        eventDetailViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let {
                        userIsPlayer = it.isPlayer()
                        setupList()
                        eventDetailViewModel.getEventDetail(eventId)
                    }
                }
            })
        eventDetailViewModel.getUser()
    }

    private fun setupView(event: EventBo) {
        viewBinding.apply {
            val called = event.call?.called ?: arrayListOf()
            eventDetailLabelTitle.text = event.title
            event.date?.let {
                eventDetailLabelDate.text = DateUtil.format(it)
            }
            eventDetailLabelType.text = event.eventType
            eventDetailLabelBody.text = event.description
            playersAdapter?.setData(called)
            playersAdapter?.notifyItemRangeChanged(0, called.size)
            if (called.isNotEmpty()) {
                viewBinding.eventDetailListCalled.visibility = VISIBLE
                viewBinding.eventDetailLabelCalled.visibility = VISIBLE
            } else {
                viewBinding.eventDetailListCalled.visibility = GONE
                viewBinding.eventDetailLabelCalled.visibility = GONE
            }
            if (event.location == null) {
                viewBinding.eventDetailBtnGoToLocation.visibility = GONE
            }

        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventDetailBinding {
        return FragmentEventDetailBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}