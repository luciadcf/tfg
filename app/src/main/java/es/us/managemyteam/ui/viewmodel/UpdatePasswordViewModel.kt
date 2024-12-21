package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.AcceptPlayerUc
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.UpdatePasswordUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdatePasswordViewModel(
    getUserUc: GetUserUc,
    private val updatePasswordUc: UpdatePasswordUc,
    private val enableUserUc: AcceptPlayerUc
) : BaseLoggedViewModel(getUserUc) {

    private val updatePassword = CustomMediatorLiveData<Resource<Boolean>>()
    private val enableUser = CustomMediatorLiveData<Resource<Boolean>>()

    fun enableUser(uuid: String) {
        viewModelScope.launch(Dispatchers.Main) {
            enableUser.setData(Resource.loading(data = null))
            withContext(Dispatchers.IO) {
                enableUser.changeSource(Dispatchers.Main, enableUserUc(uuid))
            }
        }
    }

    fun getEnableUserData(): LiveData<Resource<Boolean>> {
        //enableUser.setData(null)
        return enableUser.liveData()
    }

    fun getUpdatePasswordData(): LiveData<Resource<Boolean>> {
        //updatePassword.setData(null)
        return updatePassword.liveData()
    }

    fun getUpdatePassword(currentPassword: String, password: String, passwordRepeated: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(currentPassword, password, passwordRepeated)) {
                updatePassword.setData(Resource.loading(data = null))
                withContext(Dispatchers.IO) {
                    updatePassword.changeSource(
                        Dispatchers.Main,
                        updatePasswordUc(currentPassword, password)
                    )
                }
            }
        }
    }

    private fun validateForm(
        currentPassword: String,
        password: String,
        passwordRepeated: String
    ): Boolean {
        return when {
            password.isBlank() || passwordRepeated.isBlank() -> {
                updatePassword.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_empty_fields
                        )
                    )
                )
                false
            }

            password != passwordRepeated -> {
                updatePassword.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_invalid_password
                        )
                    )
                )
                false
            }
            currentPassword == password -> {
                updatePassword.setData(
                    Resource.error(
                        Error(
                            R.string.update_password_same_passwords
                        )
                    )
                )
                false
            }

            else -> true
        }
    }
}