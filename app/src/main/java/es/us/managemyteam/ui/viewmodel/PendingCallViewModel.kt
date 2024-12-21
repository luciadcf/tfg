package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.AcceptCallUc
import es.us.managemyteam.usecase.GetCallsByUserIdUC
import es.us.managemyteam.usecase.RejectCallUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PendingCallViewModel(
    private val getCallsByUserIdUC: GetCallsByUserIdUC,
    private val acceptCallUc: AcceptCallUc,
    private val rejectCallUc: RejectCallUc
) : ViewModel() {

    private val pendingCalls = CustomMediatorLiveData<Resource<List<EventBo>>>()
    private val acceptCallData = CustomMediatorLiveData<Resource<Boolean>>()
    private val rejectCallData = CustomMediatorLiveData<Resource<Boolean>>()
    private val auth = FirebaseAuth.getInstance()

    fun getPendingCalls() {
        viewModelScope.launch(Dispatchers.Main) {
            pendingCalls.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                pendingCalls.changeSource(
                    Dispatchers.Main,
                    getCallsByUserIdUC(auth.currentUser?.uid ?: "", CallStatus.PENDING)
                )
            }
        }
    }

    fun getPendingCallsData(): LiveData<Resource<List<EventBo>>> {
        return pendingCalls.liveData()
    }

    fun acceptCall(uuid: String) {
        viewModelScope.launch(Dispatchers.Main) {
            acceptCallData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                acceptCallData.changeSource(
                    Dispatchers.Main,
                    acceptCallUc(uuid, auth.currentUser?.uid ?: "", false)
                )
            }
        }
    }

    fun getAcceptCallData(): LiveData<Resource<Boolean>> {
        //acceptCallData.setData(null)
        return acceptCallData.liveData()
    }

    fun rejectCall(uuid: String, observation: String) {
        viewModelScope.launch(Dispatchers.Main) {
            rejectCallData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                rejectCallData.changeSource(
                    Dispatchers.Main,
                    rejectCallUc(uuid, auth.currentUser?.uid ?: "", observation, false)
                )
            }
        }
    }

    fun getRejectCallData(): LiveData<Resource<Boolean>> {
        //rejectCallData.setData(null)
        return rejectCallData.liveData()
    }

}