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
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RejectedCallViewModel(
    private val getCallsByUserIdUC: GetCallsByUserIdUC,
    private val acceptCallUc: AcceptCallUc
) : ViewModel() {

    private val rejectedCalls = CustomMediatorLiveData<Resource<List<EventBo>>>()
    private val acceptCallData = CustomMediatorLiveData<Resource<Boolean>>()
    private val auth = FirebaseAuth.getInstance()

    fun getRejectedCalls() {
        viewModelScope.launch(Dispatchers.Main) {
            rejectedCalls.setData(Resource.loading())
            withContext(Dispatchers.IO) {
                rejectedCalls.changeSource(
                    Dispatchers.Main,
                    getCallsByUserIdUC(auth.currentUser?.uid ?: "", CallStatus.DENIED)
                )
            }
        }
    }

    fun getRejectedCallsData(): LiveData<Resource<List<EventBo>>> {
        return rejectedCalls.liveData()
    }

    fun acceptCall(uuid: String) {
        viewModelScope.launch(Dispatchers.Main) {
            acceptCallData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                acceptCallData.changeSource(
                    Dispatchers.Main,
                    acceptCallUc(uuid, auth.currentUser?.uid ?: "", true)
                )
            }
        }
    }

    fun getAcceptCallData(): LiveData<Resource<Boolean>> {
       // acceptCallData.setData(null)
        return acceptCallData.liveData()
    }

}