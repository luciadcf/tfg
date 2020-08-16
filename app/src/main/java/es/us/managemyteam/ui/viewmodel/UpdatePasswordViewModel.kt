package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.viewModelScope
import es.us.managemyteam.R
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.UpdatePasswordUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdatePasswordViewModel(
    getUserUc: GetUserUc,
    private val updatePasswordUc: UpdatePasswordUc
) : BaseLoggedViewModel(getUserUc) {

    private val updatePassword = CustomMediatorLiveData<Resource<Boolean>>()

    fun getUpdatePasswordData() = updatePassword.liveData()

    fun getUpdatePassword(currentPassword: String, password: String, passwordRepeated: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(password, passwordRepeated)) {
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

    private fun validateForm(password: String, passwordRepeated: String): Boolean {
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

            else -> true
        }
    }
}