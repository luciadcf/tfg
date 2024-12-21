package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.UpdateUserProfileUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateUserProfileViewModel(
    getUserUc: GetUserUc,
    private val updateUserProfileUc: UpdateUserProfileUc
) : BaseLoggedViewModel(getUserUc) {

    private val updateUserProfile = CustomMediatorLiveData<Resource<Boolean>>()

    fun getUpdateUserProfileData() = updateUserProfile.liveData()

    fun getUpdateUserProfile(
        name: String,
        surname: String,
        phoneNumber: String,
        age: Int?,
        dorsal: Long?
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(name, surname, phoneNumber)) {
                updateUserProfile.setData(Resource.loading(data = null))
                withContext(Dispatchers.IO) {
                    updateUserProfile.changeSource(
                        Dispatchers.Main,
                        updateUserProfileUc(name, surname, phoneNumber, age, dorsal)
                    )
                }
            }
        }
    }

    private fun validateForm(
        name: String,
        surname: String,
        phoneNumber: String
    ): Boolean {
        return when {
            name.isBlank() || surname.isBlank() || phoneNumber.isBlank() -> {
                updateUserProfile.setData(
                    Resource.error(
                        Error(
                            R.string.update_user_fields_not_filled
                        )
                    )
                )
                false
            }
            else -> true
        }
    }

}