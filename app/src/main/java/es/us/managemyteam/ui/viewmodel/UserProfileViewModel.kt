package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.RemoveUserUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel(
    getUserUc: GetUserUc,
    private val removeUserUc: RemoveUserUc
) : BaseLoggedViewModel(getUserUc) {

    private val removeUserData = CustomMediatorLiveData<Resource<Boolean>>()

    fun removeUser(uuid: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            removeUserData.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                removeUserData.changeSource(Dispatchers.Main, removeUserUc(uuid, password))
            }
        }
    }

    fun getRemoveUserData(): LiveData<Resource<Boolean>> {
        //removeUserData.setData(null)
        return removeUserData.liveData()
    }

}


