package es.us.managemyteam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.extension.isEmail
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.RecoverPasswordUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecoverPasswordViewModel(
    private val recoverPasswordUc: RecoverPasswordUc
) : ViewModel() {

    private val recoverPasswordData = CustomMediatorLiveData<Resource<Boolean>>()

    fun getRecoverPasswordData() = recoverPasswordData.liveData()

    fun getRecoverPassword(email: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (validateForm(email)) {
                recoverPasswordData.setData(Resource.loading(data = null))
                withContext(Dispatchers.IO) {
                    recoverPasswordData.changeSource(
                        Dispatchers.Main,
                        recoverPasswordUc(email)
                    )
                }
            }
        }
    }

    private fun validateForm(email: String): Boolean {
        return when {
            email.isBlank() -> {
                recoverPasswordData.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_empty_fields
                        )
                    )
                )
                false
            }
            !email.isEmail() -> {
                recoverPasswordData.setData(
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