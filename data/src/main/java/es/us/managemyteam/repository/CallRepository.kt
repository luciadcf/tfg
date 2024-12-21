package es.us.managemyteam.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.us.managemyteam.data.database.DatabaseTables
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.data.model.UserCalledBo
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.RepositoryUtil
import es.us.managemyteam.repository.util.Resource
import java.util.*

interface CallRepository {

    suspend fun getAcceptedCallsByUserId(
        userUuid: String
    ): LiveData<Resource<List<EventBo>>>

    suspend fun getPendingCallsByUserId(
        userUuid: String
    ): LiveData<Resource<List<EventBo>>>

    suspend fun getRejectedCallsByUserId(
        userUuid: String
    ): LiveData<Resource<List<EventBo>>>

    suspend fun acceptCallFromPending(uuid: String, userUuid: String): LiveData<Resource<Boolean>>

    suspend fun rejectCallFromPending(
        uuid: String,
        userUuid: String,
        observation: String
    ): LiveData<Resource<Boolean>>

    suspend fun acceptCallFromRejection(uuid: String, userUuid: String): LiveData<Resource<Boolean>>

    suspend fun rejectCallFromAcceptation(
        uuid: String,
        userUuid: String,
        observation: String
    ): LiveData<Resource<Boolean>>

}

class CallRepositoryImpl : CallRepository {

    private val eventTable = RepositoryUtil.getDatabaseTable(DatabaseTables.EVENT_TABLE)
    private val callAcceptedPendingData = MutableLiveData<Resource<Boolean>>()
    private val callRejectedPendingData = MutableLiveData<Resource<Boolean>>()
    private val callAcceptedRejectData = MutableLiveData<Resource<Boolean>>()
    private val callRejectedAcceptData = MutableLiveData<Resource<Boolean>>()
    private val pendingCallsData = MutableLiveData<Resource<List<EventBo>>>()
    private val acceptedCallsData = MutableLiveData<Resource<List<EventBo>>>()
    private val rejectedCallsData = MutableLiveData<Resource<List<EventBo>>>()

    override suspend fun getAcceptedCallsByUserId(userUuid: String): LiveData<Resource<List<EventBo>>> {
        //acceptedCallsData.postValue(null)
        eventTable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                acceptedCallsData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val calls =
                    snapshot.children.mapNotNull { it.getValue(EventBo::class.java) }
                        .filter {
                            it.call?.called?.find { call ->
                                call.userId == userUuid &&
                                        call.enable == CallStatus.ACCEPTED.ordinal
                            } != null
                        }
                        .sortedBy {
                            it.date?.time ?: 0L
                        }
                acceptedCallsData.value = Resource.success(calls)
            }

        })

        return acceptedCallsData
    }

    override suspend fun getPendingCallsByUserId(userUuid: String): LiveData<Resource<List<EventBo>>> {
        //pendingCallsData.postValue(null)
        eventTable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                pendingCallsData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val calls =
                    snapshot.children.mapNotNull { it.getValue(EventBo::class.java) }
                        .filter {
                            it.call?.called?.find { call ->
                                call.userId == userUuid &&
                                        call.enable == CallStatus.PENDING.ordinal
                            } != null && it.date?.after(Date()) ?: false
                        }
                        .sortedBy {
                            it.date?.time ?: 0L
                        }
                pendingCallsData.value = Resource.success(calls)
            }

        })

        return pendingCallsData
    }

    override suspend fun getRejectedCallsByUserId(userUuid: String): LiveData<Resource<List<EventBo>>> {
        //rejectedCallsData.postValue(null)
        eventTable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                rejectedCallsData.value = Resource.error(Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val calls =
                    snapshot.children.mapNotNull { it.getValue(EventBo::class.java) }
                        .filter {
                            it.call?.called?.find { call ->
                                call.userId == userUuid &&
                                        call.enable == CallStatus.DENIED.ordinal
                            } != null
                        }
                        .sortedBy {
                            it.date?.time ?: 0L
                        }
                rejectedCallsData.value = Resource.success(calls)
            }

        })

        return rejectedCallsData
    }

    override suspend fun acceptCallFromPending(
        uuid: String,
        userUuid: String
    ): LiveData<Resource<Boolean>> {
        //callAcceptedPendingData.postValue(null)
        val callRef = eventTable.child(uuid).child("call").child("called")
        callRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                callAcceptedPendingData.value =
                    Resource.error(error = Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers =
                    snapshot.children.mapNotNull { it.getValue(UserCalledBo::class.java) }
                val userToReject = allUsers.find { it.userId == userUuid }
                val index = allUsers.indexOf(userToReject)

                callRef.child(index.toString()).updateChildren(
                    mapOf(
                        Pair(
                            "enable", CallStatus.ACCEPTED.ordinal
                        )
                    )
                )

                callAcceptedPendingData.value = Resource.success(true)
                callRef.removeEventListener(this)

            }
        })

        return callAcceptedPendingData
    }

    override suspend fun rejectCallFromPending(
        uuid: String,
        userUuid: String,
        observation: String
    ): LiveData<Resource<Boolean>> {
        //callRejectedPendingData.postValue(null)
        val callRef = eventTable.child(uuid).child("call").child("called")
        callRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                callRejectedPendingData.value =
                    Resource.error(error = Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers =
                    snapshot.children.mapNotNull { it.getValue(UserCalledBo::class.java) }
                val userToReject = allUsers.find { it.userId == userUuid }
                val index = allUsers.indexOf(userToReject)

                callRef.child(index.toString()).updateChildren(
                    mapOf(
                        Pair(
                            "enable", CallStatus.DENIED.ordinal
                        ),
                        Pair("observation", observation)

                    )
                )

                callRejectedPendingData.value = Resource.success(true)
                callRef.removeEventListener(this)

            }
        })

        return callRejectedPendingData
    }

    override suspend fun acceptCallFromRejection(
        uuid: String,
        userUuid: String
    ): LiveData<Resource<Boolean>> {
        //callAcceptedRejectData.postValue(null)
        val callRef = eventTable.child(uuid).child("call").child("called")
        callRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                callAcceptedRejectData.value =
                    Resource.error(error = Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers =
                    snapshot.children.mapNotNull { it.getValue(UserCalledBo::class.java) }
                val userToReject = allUsers.find { it.userId == userUuid }
                val index = allUsers.indexOf(userToReject)

                callRef.child(index.toString()).updateChildren(
                    mapOf(
                        Pair(
                            "enable", CallStatus.ACCEPTED.ordinal
                        )
                    )
                )

                callAcceptedRejectData.value = Resource.success(true)
                callRef.removeEventListener(this)
            }
        })

        return callAcceptedRejectData
    }

    override suspend fun rejectCallFromAcceptation(
        uuid: String,
        userUuid: String,
        observation: String
    ): LiveData<Resource<Boolean>> {
        //callRejectedAcceptData.postValue(null)
        val callRef = eventTable.child(uuid).child("call").child("called")
        callRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                callRejectedAcceptData.value =
                    Resource.error(error = Error(serverErrorMessage = error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers =
                    snapshot.children.mapNotNull { it.getValue(UserCalledBo::class.java) }
                val userToReject = allUsers.find { it.userId == userUuid }
                val index = allUsers.indexOf(userToReject)

                callRef.child(index.toString()).updateChildren(
                    mapOf(
                        Pair(
                            "enable", CallStatus.DENIED.ordinal
                        ),
                        Pair("observation", observation)

                    )
                )

                callRejectedAcceptData.value = Resource.success(true)
                callRef.removeEventListener(this)

            }
        })

        return callRejectedAcceptData
    }

}