package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import es.us.managemyteam.data.model.CallStatus
import es.us.managemyteam.data.model.EventBo
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetCallsByUserIdUC
import es.us.managemyteam.usecase.RejectCallUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AcceptedCallViewModel(
    private val getCallsByUserIdUC: GetCallsByUserIdUC,
    private val rejectCallUc: RejectCallUc
) : ViewModel() {

    private val acceptedCalls = CustomMediatorLiveData<Resource<List<EventBo>>>()
    private val rejectCallData = CustomMediatorLiveData<Resource<Boolean>>()
    private val auth = FirebaseAuth.getInstance()

    fun getAcceptedCalls() {
        viewModelScope.launch(Dispatchers.Main) {
            acceptedCalls.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                acceptedCalls.changeSource(
                    Dispatchers.Main,
                    getCallsByUserIdUC(auth.currentUser?.uid ?: "", CallStatus.ACCEPTED)
                )
            }
        }
    }

    fun getAcceptedCallsData(): LiveData<Resource<List<EventBo>>> {
        return acceptedCalls.liveData()
    }

    fun rejectCall(uuid: String, observation: String) {
        viewModelScope.launch(Dispatchers.Main) {
            rejectCallData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                rejectCallData.changeSource(
                    Dispatchers.Main,
                    rejectCallUc(uuid, auth.currentUser?.uid ?: "", observation, true)
                )
            }
        }
    }

    fun getRejectCallData(): LiveData<Resource<Boolean>> {
        //rejectCallData.setData(null)
        return rejectCallData.liveData()
    }

}