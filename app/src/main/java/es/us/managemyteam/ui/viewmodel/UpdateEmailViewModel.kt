package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.extension.isEmail
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.GetUserUc
import es.us.managemyteam.usecase.UpdateEmailUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateEmailViewModel(
    getUserUc: GetUserUc,
    private val updateEmailUc: UpdateEmailUc
) : BaseLoggedViewModel(getUserUc) {

    private val updateEmail = CustomMediatorLiveData<Resource<Boolean>>()

    fun getUpdateEmailData() = updateEmail.liveData()

    fun getUpdateEmail(currentPassword: String, email: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(currentPassword, email)) {
                updateEmail.setData(Resource.loading(data = null))
                withContext(Dispatchers.IO) {
                    updateEmail.changeSource(
                        Dispatchers.Main,
                        updateEmailUc(currentPassword, email)
                    )
                }
            }
        }
    }

    private fun validateForm(currentPassword: String, email: String): Boolean {
        return when {
            currentPassword.isBlank() || email.isBlank() -> {
                updateEmail.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_empty_fields
                        )
                    )
                )
                false
            }
            !email.isEmail() -> {
                updateEmail.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_invalid_email
                        )
                    )
                )
                false
            }

            else -> true
        }
    }
}