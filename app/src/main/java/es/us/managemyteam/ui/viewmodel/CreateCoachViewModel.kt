package es.us.managemyteam.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.managemyteam.app.R
import es.us.managemyteam.constant.RegistrationError
import es.us.managemyteam.data.model.Role
import es.us.managemyteam.extension.isEmail
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.Resource
import es.us.managemyteam.usecase.PostRegistrationUc
import es.us.managemyteam.util.CustomMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateCoachViewModel(
    private val postRegistrationUc: PostRegistrationUc
) : ViewModel() {

    private val createCoachData = CustomMediatorLiveData<Resource<Boolean>>()

    fun getCreateCoachData() = createCoachData.liveData()

    fun createCoach(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        surname: String,
        phoneNumber: String,
        role: Role
    ) = viewModelScope.launch {
        createCoachData.setData(Resource.loading())
        if (validateForm(email, password, confirmPassword, name, surname, phoneNumber)) {
            withContext(Dispatchers.IO) {
                createCoachData.changeSource(
                    Dispatchers.Main,
                    postRegistrationUc(email, password, name, surname, phoneNumber, role)
                )
            }
        }
    }

    private fun validateForm(
        email: String,
        password: String,
        passwordRepeated: String,
        name: String,
        surname: String,
        phoneNumber: String
    ): Boolean {
        return when {
            name.isBlank() || password.isBlank() || passwordRepeated.isBlank() || name.isBlank() || surname.isBlank() || phoneNumber.isBlank() -> {
                createCoachData.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_empty_fields,
                            RegistrationError.EMPTY_FIELDS.ordinal
                        )
                    )
                )
                false
            }
            !email.isEmail() -> {
                createCoachData.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_invalid_email,
                            RegistrationError.NOT_AN_EMAIL.ordinal
                        )
                    )
                )
                false
            }
            password != passwordRepeated -> {
                createCoachData.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_invalid_password,
                            RegistrationError.PASSWORDS_NOT_FILL.ordinal
                        )
                    )
                )
                false
            }
            !Patterns.PHONE.matcher(phoneNumber).matches() -> {
                createCoachData.setData(
                    Resource.error(
                        Error(
                            R.string.registration_error_invalid_phone,
                            RegistrationError.NOT_A_PHONE.ordinal
                        )
                    )
                )
                false
            }
            else -> true
        }
    }
}