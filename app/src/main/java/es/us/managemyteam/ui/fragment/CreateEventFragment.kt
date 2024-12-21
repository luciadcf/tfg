package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentCreateEventBinding
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import es.us.managemyteam.data.model.CallBo
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.data.model.LocationBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.PlayerAdapter
import es.us.managemyteam.ui.view.common_map.MapListener
import es.us.managemyteam.ui.view.common_map.MarkerItemVo
import es.us.managemyteam.ui.viewmodel.CreateEventViewModel
import es.us.managemyteam.util.DateUtil
import es.us.managemyteam.util.EventUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class CreateEventFragment : BaseFragment<FragmentCreateEventBinding>(), MapListener {

    private var selectedDate = Calendar.getInstance()
    private var selectedEventType = ""
    private val createEventViewModel: CreateEventViewModel by viewModel()
    private var locationSelected: LocationBo? = null
    private var currentNewEvent: EventBo = EventBo()
    private val playersAdapter = PlayerAdapter(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap(savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupMap(savedInstanceState: Bundle?) {
        viewBinding.createEventViewMap.apply {
            onCreate(savedInstanceState)
            setEnableInteraction(false)
            setShowUserPosition(false)
            showLocationButton(false)
            setMapListener(this@CreateEventFragment)
        }
    }

    private fun setupObservers() {
        setupDeviceIdsObserver()
        setupSendNotificationObserver()
        setupCurrentEventObserver()
        setupCreateEventObserver()
        setupGetCurrentNewEventObserver()
        setupLocationSelectedObserver()
    }

    private fun setupClickListeners() {
        setupDateClickListener()
        setupEventTypeClickListener()
        viewBinding.createEventBtnSelectPlayers.setOnClickListener {
            setCurrentEvent()
            findNavController().navigate(R.id.action_create_event_to_select_players)
        }
        viewBinding.createEventBtnSave.setOnClickListener {
            clickOnSave()
        }
        setupLocationClickListener()
    }

    private fun setupViews() {
        if (locationSelected == null) {
            viewBinding.createEventContainerMap.visibility = GONE
            viewBinding.createEventLabelLocation.visibility = VISIBLE
        } else {
            viewBinding.createEventLabelLocation.visibility = GONE
            viewBinding.createEventContainerMap.visibility = VISIBLE
        }
    }


    //region Map

    override fun onResume() {
        super.onResume()
        viewBinding.createEventViewMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.createEventViewMap.onPause()
    }

    override fun onStart() {
        super.onStart()
        viewBinding.createEventViewMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewBinding.createEventViewMap.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewBinding.createEventViewMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.createEventViewMap.onDestroy()
        createEventViewModel.clearEvent()
        createEventViewModel.setLocationSelected(null)
    }

    //endregion

    private fun onEventSuccessfullyCreated(showMessage: Boolean) {
        createEventViewModel.clearEvent()
        if (showMessage) {
            Snackbar.make(
                viewBinding.createEventBtnSave,
                getString(R.string.create_event_success),
                Snackbar.LENGTH_LONG
            )
                .show()
        }
        popBack()
    }

    private fun showErrorMessageAfterCreationSuccessfully() {
        Snackbar.make(
            viewBinding.createEventBtnSave,
            R.string.notification_error_creation_ok, Snackbar.LENGTH_LONG
        ).apply {
            addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    onEventSuccessfullyCreated(false)
                }
            })
        }.show()
    }

    //region Observers

    private fun setupDeviceIdsObserver() {
        createEventViewModel.getDeviceIds().observe(viewLifecycleOwner, object :
            ResourceObserver<List<Pair<String, String>>>() {
            override fun onSuccess(response: List<Pair<String, String>>?) {
                response?.let {
                    createEventViewModel.sendNotification(
                        getString(R.string.app_name),
                        getString(R.string.notification_message),
                        *it.toTypedArray()
                    )
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                showErrorMessageAfterCreationSuccessfully()
            }
        })
    }

    private fun setupSendNotificationObserver() {
        createEventViewModel.getNotification().observe(viewLifecycleOwner, object :
            ResourceObserver<Boolean>() {
            override fun onSuccess(response: Boolean?) {
                response?.let {
                    if (it) {
                        onEventSuccessfullyCreated(it)
                    } else {
                        showErrorMessageAfterCreationSuccessfully()
                    }
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                showErrorMessageAfterCreationSuccessfully()
            }
        })
    }

    private fun setupCurrentEventObserver() {
        createEventViewModel.getCurrentNewEventData()
            .observe(viewLifecycleOwner, object : ResourceObserver<EventBo>() {
                override fun onSuccess(response: EventBo?) {
                    currentNewEvent = response ?: EventBo()
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    currentNewEvent = EventBo()
                }

            })
    }

    private fun setupCreateEventObserver() {
        createEventViewModel.createEventData()
            .observe(viewLifecycleOwner, object : ResourceObserver<EventBo>() {
                override fun onSuccess(response: EventBo?) {
                    response?.let { event ->
                        createEventViewModel.getDeviceIds(*event.call?.called?.map { it.userId }
                            ?.toTypedArray() ?: arrayOf())
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(
                        getString(error.errorMessageId)
                    )
                }

            })
    }

    private fun setupLocationSelectedObserver() {
        createEventViewModel.getLocationSelectedData()
            .observe(viewLifecycleOwner, object : ResourceObserver<LocationBo?>() {
                override fun onSuccess(response: LocationBo?) {
                    locationSelected = response
                    setupViews()
                    response?.let {
                        setupMinimap(it)
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }
            })
        createEventViewModel.getLocationSelected()
    }

    private fun setupGetCurrentNewEventObserver() {
        createEventViewModel.getCurrentNewEventData()
            .observe(viewLifecycleOwner, object : ResourceObserver<EventBo>() {
                override fun onSuccess(response: EventBo?) {
                    response?.let {
                        currentNewEvent = it
                        setupCurrentEvent(it)
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(
                        getString(error.errorMessageId),
                        getDefaultDialogErrorListener()
                    )
                }
            })
        createEventViewModel.getCurrentNewEvent()
    }

    private fun setupCurrentEvent(event: EventBo) {
        val called = event.call?.called ?: arrayListOf()
        viewBinding.createEventEditTextTitle.setText(event.title)
        event.date?.let {
            viewBinding.createEventEditTextDate.setText(DateUtil.format(it))
        }
        viewBinding.createEventEditTextDescription.setText(event.description)
        viewBinding.createEventEditTextEventType.setText(event.eventType)
        viewBinding.createEventListPlayers.adapter = playersAdapter
        playersAdapter.setData(called)
        playersAdapter.notifyDataSetChanged()
        viewBinding.createEventListPlayers.visibility = if (called.isNotEmpty()) {
            VISIBLE
        } else {
            GONE
        }
    }

    private fun setCurrentEvent() {
        val title = viewBinding.createEventEditTextTitle.text.trim()
        val sdf = SimpleDateFormat(getString(R.string.date_time_format), Locale.getDefault())
        val date = if (viewBinding.createEventEditTextDate.text.isNotBlank()) {
            sdf.parse(viewBinding.createEventEditTextDate.text)
        } else {
            null
        }
        val description = viewBinding.createEventEditTextDescription.text.trim()
        val eventType = viewBinding.createEventEditTextEventType.text.trim()

        currentNewEvent.apply {
            this.title = title
            this.description = description
            this.date = date
            this.eventType = eventType
            this.location = locationSelected
        }

        createEventViewModel.setCurrentNewEvent(currentNewEvent)
    }

    //endregion

    //region Clicks

    private fun clickOnSave() {
        val title = viewBinding.createEventEditTextTitle.text.trim()
        val sdf = SimpleDateFormat(getString(R.string.date_time_format), Locale.getDefault())
        val date = if (viewBinding.createEventEditTextDate.text.isNotBlank()) {
            sdf.parse(viewBinding.createEventEditTextDate.text)
        } else {
            null
        }
        val description = viewBinding.createEventEditTextDescription.text.trim()
        val eventType = viewBinding.createEventEditTextEventType.text.trim()

        getFocusedView().hideKeyboard()

        createEventViewModel.createEvent(
            title,
            date,
            eventType,
            description,
            locationSelected,
            CallBo(
                currentNewEvent.call?.called ?: listOf(),
                currentNewEvent.call?.notCalled ?: listOf(),
                Date()
            )
        )
    }

    private fun setupLocationClickListener() {
        viewBinding.createEventContainerLocation.setOnClickListener {
            openSelectLocationFragment()
        }
    }

    private fun openSelectLocationFragment() {
        setCurrentEvent()
        findNavController().navigate(R.id.action_create_event_to_select_location)
    }

    private fun setupEventTypeClickListener() {
        viewBinding.createEventEditTextEventType.clickListener {
            context?.let { context ->
                val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                val list = EventUtil.getEventTypesList(context)
                dialog.apply {
                    setTitle(getString(R.string.create_event_event_type))
                    setSingleChoiceItems(
                        list, -1
                    ) { _, which ->
                        selectedEventType = list[which]
                    }
                    setCancelable(false)
                    setPositiveButton(R.string.accept) { dialog, _ ->
                        viewBinding.createEventEditTextEventType.setText(selectedEventType)
                        dialog.cancel()
                    }
                    setNegativeButton(
                        R.string.cancel
                    ) { dialog, _ ->
                        selectedEventType = viewBinding.createEventEditTextEventType.text
                        dialog.cancel()
                    }
                    show()
                }
            }

        }
    }

    private fun setupDateClickListener() {
        viewBinding.createEventEditTextDate.clickListener {
            showDateDialog(
                selectedDate,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    updateSelectedDate(dayOfMonth, month + 1, year)
                    showTimePickerDialog(dayOfMonth, month + 1, year)
                })
        }
    }

    //endregion

    //region Dialogs

    private fun updateSelectedDate(day: Int, month: Int, year: Int) {
        selectedDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
    }

    private fun showDateDialog(
        date: Calendar,
        dateListener: DatePickerDialog.OnDateSetListener
    ) {
        val year = date[Calendar.YEAR]
        val month = date[Calendar.MONTH]
        val day = date[Calendar.DAY_OF_MONTH]

        context?.let {
            DatePickerDialog(
                it,
                R.style.DialogTheme,
                dateListener,
                year,
                month,
                day
            ).apply {
                datePicker.minDate = Date().time
                show()
            }
        }
    }

    private fun showTimePickerDialog(day: Int, month: Int, year: Int) {
        context?.let {
            TimePickerDialog(
                it,
                { _, hourOfDay, minute ->
                    viewBinding.createEventEditTextDate.setText(
                        String.format(
                            getString(R.string.create_event_date_format),
                            day,
                            month,
                            year,
                            hourOfDay,
                            minute
                        )
                    )
                }, getInstance().get(HOUR_OF_DAY),
                getInstance().get(MINUTE),
                true
            ).apply {
                setCancelable(false)
                show()
            }
        }
    }

    //endregion

    private fun setupMinimap(location: LocationBo) {
        val mapView = viewBinding.createEventViewMap
        val marker = location.location?.getMarkerItemVo(mapView)
        marker?.let {
            mapView.addMarker(it)
            viewBinding.createEventLabelAddress.text =
                mapView.getAddressFromMarkerPosition(it.position)
        }

    }

    //region BaseFragment

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateEventBinding {
        return FragmentCreateEventBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.create_event_title))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.show()
    }

    override fun onMapClicked(latLng: LatLng) {
        openSelectLocationFragment()
    }

    override fun onMarkerClicked(markerClicked: MarkerItemVo) {
        openSelectLocationFragment()
    }

    override fun onMapReady() {
        viewBinding.createEventViewMap.apply {
            initialize(this@CreateEventFragment)
        }
    }

    override fun onLocationChanged(latLng: LatLng) {
        // no-op
    }

    //endregion

}