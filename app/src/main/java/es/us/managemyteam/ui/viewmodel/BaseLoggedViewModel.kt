package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.util.CustomMediatorLiveData
import es.us.managemyteam.util.FirebaseAuthUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseLoggedViewModel(private val getUserUc: GetUserUc) : ViewModel() {

    private val auth = FirebaseAuthUtil.getFirebaseAuthInstance()
    private val user = CustomMediatorLiveData<Resource<UserBo>>()

    fun getUserData() = user.apply {
        //user.setData(null)
    }.liveData()

    fun getUser(uuid: String? = null) {
        val finalId = uuid ?: auth.currentUser?.uid
        finalId?.let { uid ->
            viewModelScope.launch(Dispatchers.Main) {
                user.setData(Resource.loading(data = null))
                withContext(Dispatchers.IO) {
                    user.changeSource(
                        Dispatchers.Main,
                        getUserUc(uid)
                    )
                }
            }
        }
    }

    fun getUserLoggedEmail() = auth.currentUser?.email ?: ""

}