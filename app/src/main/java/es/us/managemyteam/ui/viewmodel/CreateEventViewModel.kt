package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.data.model.CallBo
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.data.model.LocationBo
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.*
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CreateEventViewModel(
    private val getEventLocationUc: GetEventLocationUc,
    private val setEventLocationUc: SetEventLocationUc,
    private val getCurrentNewEventUc: GetCurrentNewEventUc,
    private val setCurrentNewEventUc: SetCurrentNewEventUc,
    private val createEventUc: CreateEventUc,
    private val getPlayersUc: GetPlayersUc,
    private val setCurrentCallUc: SetCurrentCallUc,
    private val getUserDeviceIdsUc: GetUserDeviceIdsUc,
    private val sendNotificationsUc: SendNotificationsUc
) : ViewModel() {

    private val currentNewEvent = CustomMediatorLiveData<Resource<EventBo>>()
    private val createEvent = CustomMediatorLiveData<Resource<EventBo>>()
    private val locationSelected = CustomMediatorLiveData<Resource<LocationBo?>>()
    private val players = CustomMediatorLiveData<Resource<List<UserBo>>>()
    private val deviceIds = CustomMediatorLiveData<Resource<List<Pair<String, String>>>>()
    private val notificationSent = CustomMediatorLiveData<Resource<Boolean>>()

    fun getPlayers() {
        viewModelScope.launch(Dispatchers.Main) {
            players.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                players.changeSource(Dispatchers.Main, getPlayersUc(true))
            }
        }
    }

    fun getPlayersData(): LiveData<Resource<List<UserBo>>> {
        return players.liveData()
    }

    fun getDeviceIds(): LiveData<Resource<List<Pair<String, String>>>> {
        //deviceIds.setData(null)
        return deviceIds.liveData()
    }

    fun getNotification(): LiveData<Resource<Boolean>> {
        //notificationSent.setData(null)
        return notificationSent.liveData()
    }

    fun createEvent(
        title: String,
        date: Date?,
        type: String,
        description: String?,
        location: LocationBo?,
        call: CallBo?
    ) =
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(title, date, type)) {
                withContext(Dispatchers.IO) {
                    createEvent.changeSource(
                        Dispatchers.Main,
                        createEventUc(title, date, type, description, location, call)
                    )
                }
            }
        }

    fun sendNotification(
        title: String,
        message: String,
        vararg deviceIds: Pair<String, String>
    ) = viewModelScope.launch(Dispatchers.IO) {
        notificationSent.changeSource(
            Dispatchers.Main,
            sendNotificationsUc(
                title,
                message,
                *deviceIds
            )
        )

    }

    fun getDeviceIds(vararg userIds: String) = viewModelScope.launch(Dispatchers.IO) {
        deviceIds.changeSource(
            Dispatchers.Main,
            getUserDeviceIdsUc(*userIds)
        )
    }

    fun createEventData(): LiveData<Resource<EventBo>> {
        //createEvent.setData(null)
        return createEvent.liveData()
    }

    private fun validateForm(
        title: String,
        date: Date?,
        type: String
    ): Boolean {
        return when {
            title.isBlank() -> {
                createEvent.setData(
                    Resource.error(
                        Error(R.string.create_event_error_title)
                    )
                )
                false
            }
            date == null -> {
                createEvent.setData(
                    Resource.error(
                        Error(R.string.create_event_error_date)
                    )
                )
                false
            }
            type.isBlank() -> {
                createEvent.setData(
                    Resource.error(
                        Error(R.string.create_event_error_event_type)
                    )
                )
                false
            }
            date.before(Calendar.getInstance().time) -> {
                createEvent.setData(
                    Resource.error(
                        Error(R.string.create_event_error_time)
                    )
                )
                false
            }
            else -> true
        }
    }

    fun getCurrentNewEvent() =
        viewModelScope.launch(Dispatchers.Main) {
            currentNewEvent.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                currentNewEvent.changeSource(Dispatchers.Main, getCurrentNewEventUc())
            }
        }

    fun getCurrentNewEventData() = currentNewEvent.liveData()

    fun setCurrentNewEvent(event: EventBo) =
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                setCurrentNewEventUc(event)
            }
        }

    fun clearEvent() =
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                setCurrentNewEventUc(null)
                setLocationSelected(null)
                setCurrentCallUc(null)
            }
        }

    fun getLocationSelected() =
        viewModelScope.launch(Dispatchers.Main) {
            locationSelected.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                locationSelected.changeSource(Dispatchers.Main, getEventLocationUc())
            }
        }

    fun getLocationSelectedData() = locationSelected.liveData()

    fun setLocationSelected(location: LocationBo?) =
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                setEventLocationUc(location)
            }
        }
}