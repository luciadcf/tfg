package es.us.managemyteam.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.us.managemyteam.data.database.DatabaseTables
import es.us.managemyteam.data.model.CallBo
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.data.model.LocationBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.RepositoryUtil
import es.us.managemyteam.repository.util.Resource
import java.util.*

interface EventRepository {

    suspend fun getEvents(pastEvents: Boolean): LiveData<Resource<List<EventBo>>>

    suspend fun getEventLocation(): LocationBo?

    suspend fun setEventLocation(location: LocationBo?)

    suspend fun getCurrentNewEvent(): EventBo?

    suspend fun setCurrentNewEvent(event: EventBo?)

    suspend fun createEvent(
        title: String,
        date: Date?,
        type: String,
        description: String?,
        location: LocationBo?,
        call: CallBo?
    ): LiveData<Resource<EventBo>>

    suspend fun getEventDetail(uuid: String): LiveData<Resource<EventBo>>

    suspend fun getCurrentCall(): CallBo?

    suspend fun setCurrentCall(call: CallBo?)

}

class EventRepositoryImpl : EventRepository {

    private var eventLocation: LocationBo? = null
    private val eventsRef = RepositoryUtil.getDatabaseTable(DatabaseTables.EVENT_TABLE)
    private val events = MutableLiveData<Resource<List<EventBo>>>()
    private var currentEvent: EventBo? = EventBo()
    private var currentCall: CallBo? = CallBo()
    private val eventCreateData = MutableLiveData<Resource<EventBo>>()
    private val eventDetail = MutableLiveData<Resource<EventBo>>()

    override suspend fun getEventLocation(): LocationBo? {
        return eventLocation
    }

    override suspend fun setEventLocation(location: LocationBo?) {
        this.eventLocation = location
    }

    override suspend fun getCurrentNewEvent(): EventBo? {
        return currentEvent
    }

    override suspend fun setCurrentNewEvent(event: EventBo?) {
        currentEvent = event
    }

    override suspend fun createEvent(
        title: String,
        date: Date?,
        type: String,
        description: String?,
        location: LocationBo?,
        call: CallBo?
    ): LiveData<Resource<EventBo>> {
        val eventToCreate = EventBo(
            title, date, location, description, call, type
        )
        eventsRef.push().setValue(eventToCreate) { databaseError, ref ->
            if (databaseError != null) {
                eventCreateData.value =
                    Resource.error(Error(serverErrorMessage = databaseError.message))
            } else {
                ref.updateChildren(
                    mapOf(
                        Pair("uuid", ref.key)
                    )
                )
                eventCreateData.value = Resource.success(eventToCreate)
            }
        }
        return eventCreateData
    }

    override suspend fun getEventDetail(uuid: String): LiveData<Resource<EventBo>> {
        //eventDetail.postValue(null)
        eventsRef.child(uuid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                eventDetail.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                eventDetail.value = Resource.success(snapshot.getValue(EventBo::class.java))
            }

        })
        return eventDetail
    }

    override suspend fun getCurrentCall(): CallBo? {
        return currentCall
    }

    override suspend fun setCurrentCall(call: CallBo?) {
        this.currentCall = call
    }

    override suspend fun getEvents(pastEvents: Boolean): LiveData<Resource<List<EventBo>>> {
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                events.value = Resource.error(Error(serverErrorMessage = databaseError.message))
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allEvents =
                    dataSnapshot.children.mapNotNull { it.getValue(EventBo::class.java) }
                events.value = if (pastEvents) {
                    Resource.success(allEvents.filter { it.date != null && it.date!!.before(Date()) })
                } else {
                    Resource.success(allEvents.filter { it.date != null && it.date!!.after(Date()) })
                }
            }

        })
        return events
    }

}